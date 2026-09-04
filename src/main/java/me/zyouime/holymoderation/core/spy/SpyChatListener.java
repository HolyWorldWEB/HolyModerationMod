package me.zyouime.holymoderation.core.spy;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.service.SpyService;
import me.zyouime.holymoderation.core.states.UserState;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;

import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public record SpyChatListener(SpyService spyService, UserState userState, ChatService chatService, ModSettings settings, LoggerService logger) {

    private static final String OFFLINE_STATUS = "offline";

    private Optional<ServerLocation> parseDisplayLocation(String raw) {
        Optional<ServerLocation> parsed = ServerLocation.parseDisplay(raw);
        if (parsed.isEmpty()) {
            logger.debug("Неизвестная локация из /playtime: '%s'".formatted(raw));
        }
        return parsed;
    }

    public ActionResult onMessage(Text message) {
        SpySession session = spyService.sessionOrNull();
        if (session == null || (!session.isAwaitingResponse() && !session.isProcessingPlaytimeInfo())) {
            return ActionResult.PASS;
        }
        String text = chatService.formatReceivedText(message.getString());
        if (text == null) {
            return ActionResult.PASS;
        }
        boolean hide = false;
        boolean blockFinished = false;
        boolean instantUpdate = false;
        if (text.startsWith(HolyWorldPatterns.PLAYTIME_SEPARATOR)) {
            if (session.isProcessingPlaytimeInfo()) {
                session.processingPlaytimeInfo(false);
                spyService.onPlaytimeComplete();
                blockFinished = true;
            } else {
                session.processingPlaytimeInfo(true);
            }
        }
        if (HolyWorldPatterns.isFindResponseAboutOther(text, userState.getUserNickname())) {
            hide = true;
            handleFindResponse(text);
            blockFinished = true;
        }
        String playtimeLocation = HolyWorldPatterns.extractPlaytimeLocation(text);
        if (playtimeLocation != null) {
            instantUpdate = handlePlaytimeLocation(session, playtimeLocation);
        }
        if (text.startsWith(HolyWorldPatterns.PLAYTIME_LAST_PREFIX) && session.getStatus().isKnown()) {
            session.activity(HolyWorldPatterns.extractPlaytimeActivity(text));
        }
        if (HolyWorldPatterns.isPlaytimeNoise(text)) {
            hide = true;
        }
        if (blockFinished) {
            planNextUpdate(session, instantUpdate);
        }
        if (hide) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private void handleFindResponse(String text) {
        String raw = HolyWorldPatterns.parseFindStatus(text);
        if (raw == null) {
            logger.debug("Не удалось разобрать ответ /find: %s".formatted(text));
            spyService.onFindResponse(SpyStatus.unknown());
            return;
        }
        if (raw.equals(OFFLINE_STATUS)) {
            spyService.onFindResponse(SpyStatus.offline());
            return;
        }
        Optional<ServerLocation> parsed = parseLocation(raw);
        if (parsed.isEmpty()) {
            spyService.onFindResponse(SpyStatus.unknown());
            return;
        }
        spyService.onFindResponse(SpyStatus.online(parsed.get()));
    }

    private boolean handlePlaytimeLocation(SpySession session, String playtimeLocation) {
        if (playtimeLocation.equals(HolyWorldPatterns.OFFLINE_LITERAL)) {
            spyService.onFindResponse(SpyStatus.offline());
            return true;
        }
        Optional<ServerLocation> parsed = parseDisplayLocation(playtimeLocation);
        if (parsed.isEmpty()) {
            return false;
        }
        ServerLocation location = parsed.get();
        Optional<ServerLocation> last = session.knownLocation();
        if (last.isEmpty()) {
            session.lastKnownLocation(location);
            return false;
        }
        if (!last.get().equals(location)) {
            session.status(SpyStatus.unknown());
            session.lastKnownLocation(location);
        }
        return false;
    }

    private Optional<ServerLocation> parseLocation(String raw) {
        Optional<ServerLocation> parsed = ServerLocation.parse(raw);
        if (parsed.isEmpty()) {
            logger.debug("Неизвестная локация сервера: '%s'".formatted(raw));
        }
        return parsed;
    }

    private void planNextUpdate(SpySession session, boolean instantUpdate) {
        boolean instant = instantUpdate;
        if (session.isRightHere(spyService.userLocation())) {
            instant = true;
            if (settings.autoSpyTp.getValue()) {
                chatService.chatMessage("/tpo %s".formatted(session.getPlayer()));
            }
        }
        if (instant) {
            spyService.scheduleUpdate(SpyService.INSTANT_DELAY_TICKS);
            return;
        }
        spyService.scheduleUpdate();
    }
}
