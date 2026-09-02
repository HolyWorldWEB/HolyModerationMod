package me.zyouime.holymoderation.core.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.obs.ObsConnection;
import me.zyouime.holymoderation.core.obs.ObsMessage;

public final class ObsService {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    private static final Pattern UNSAFE_NAME = Pattern.compile("[^\\p{L}\\p{N}_.-]");
    private static final int MAX_NAME_LENGTH = 64;
    private final ModSettings settings;
    private final NotificationsService notifications;
    private final LoggerService logger;
    private final ObsConnection connection;
    private volatile String pendingName;

    public ObsService(ModSettings settings, NotificationsService notifications, LoggerService logger) {
        this.settings = settings;
        this.notifications = notifications;
        this.logger = logger;
        this.connection = new ObsConnection(logger, this::onEvent);
    }

    public void startRecording(String suspect) {
        if (!settings.obsEnabled.getValue()) {
            return;
        }
        int obsPort;
        try {
            obsPort = Integer.parseInt(settings.obsPort.getValue());
        } catch (NumberFormatException e) {
            notifications.error("Неверно введен порт. Использую стандартный 4455");
            obsPort = 4455;
        }
        pendingName = buildName(suspect);
        connection.connect(settings.obsHost.getValue(), obsPort, settings.obsPassword.getValue())
                .thenCompose(ignored -> connection.request(ObsMessage.START_RECORD, null))
                .whenComplete((response, error) -> {
                    if (error != null) {
                        pendingName = null;
                        notifications.error("OBS: не удалось начать запись.");
                        logger.exception("OBS: старт записи не удался: " + error);
                        return;
                    }
                    notifications.success("OBS: запись начата.");
                });
    }

    public void stopRecording() {
        if (!connection.isConnected()) {
            pendingName = null;
            return;
        }
        connection.request(ObsMessage.STOP_RECORD, null).whenComplete((response, error) -> {
            if (error != null) {
                pendingName = null;
                notifications.error("OBS: не удалось остановить запись.");
                logger.exception("OBS: остановка записи не удалась: " + error);
            }
        });
    }

    public void disconnect() {
        pendingName = null;
        connection.disconnect();
    }

    private void onEvent(JsonObject event) {
        if (!ObsMessage.RECORD_STATE_CHANGED.equals(event.get("eventType").getAsString())) {
            return;
        }
        JsonObject data = event.getAsJsonObject("eventData");
        if (!ObsMessage.OUTPUT_STOPPED.equals(data.get("outputState").getAsString())) {
            return;
        }
        String name = pendingName;
        pendingName = null;
        if (name == null || !data.has("outputPath")) {
            return;
        }
        rename(Path.of(data.get("outputPath").getAsString()), name);
    }

    private void rename(Path recorded, String name) {
        String extension = extensionOf(recorded);
        Path target = recorded.resolveSibling(name + extension);
        try {
            Files.move(recorded, target, StandardCopyOption.REPLACE_EXISTING);
            notifications.success("OBS: запись сохранена как %s".formatted(target.getFileName()));
        } catch (IOException e) {
            notifications.warning("OBS: запись сохранена, но переименовать файл не удалось.");
            logger.exception("OBS: переименование %s не удалось: %s".formatted(recorded, e));
        }
    }

    private static String extensionOf(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot);
    }

    private static String buildName(String suspect) {
        String safe = UNSAFE_NAME.matcher(suspect).replaceAll("_");
        if (safe.length() > MAX_NAME_LENGTH) {
            safe = safe.substring(0, MAX_NAME_LENGTH);
        }
        return "%s_%s".formatted(safe, LocalDateTime.now().format(STAMP));
    }
}