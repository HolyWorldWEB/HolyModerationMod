package me.zyouime.holymoderation.core.command.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.settings.entry.NumberEntry;
import me.zyouime.holymoderation.core.settings.entry.SettingEntry;
import me.zyouime.holymoderation.core.settings.SettingsEditor;
import me.zyouime.holymoderation.core.settings.entry.TextEntry;
import me.zyouime.holymoderation.core.settings.entry.ToggleEntry;
import me.zyouime.holymoderation.core.util.Colors;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public final class SettingsCommand implements ModCommand {

    private static final String SETTING_ARG = "настройка";
    private static final String VALUE_ARG = "значение";
    private static final SimpleCommandExceptionType UNKNOWN_SETTING = Cmd.error("Такой настройки нет. Список: /hm settings");
    private static final SimpleCommandExceptionType VALUE_REQUIRED = Cmd.error("Укажите значение.");
    private static final SimpleCommandExceptionType NO_VALUE_EXPECTED = Cmd.error("Эта настройка переключается без значения.");
    private final ChatService chatService;
    private final NotificationsService notifications;
    private final SettingsEditor editor;
    private final Map<String, SettingEntry<?>> entries;

    public SettingsCommand(SettingsEditor editor, List<SettingEntry<?>> catalog, ChatService chatService, NotificationsService notifications) {
        this.editor = editor;
        this.chatService = chatService;
        this.notifications = notifications;
        this.entries = new LinkedHashMap<>();
        for (SettingEntry<?> entry : catalog) {
            entries.put(entry.key(), entry);
        }
    }

    @Override
    public String name() {
        return "settings";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        List<String> keys = List.copyOf(entries.keySet());
        node.executes(exec.of(context -> list()))
                .then(Cmd.sub("reset")
                        .then(Cmd.choice(SETTING_ARG, keys)
                                .executes(exec.of(context -> reset(Cmd.str(context, SETTING_ARG))))))
                .then(Cmd.choice(SETTING_ARG, keys)
                        .executes(exec.of(context -> apply(Cmd.str(context, SETTING_ARG), null)))
                        .then(Cmd.text(VALUE_ARG)
                                .executes(exec.of(context ->
                                        apply(Cmd.str(context, SETTING_ARG), Cmd.str(context, VALUE_ARG))))));
    }

    private void apply(String key, String raw) throws CommandSyntaxException {
        SettingEntry<?> entry = lookup(key);
        switch (entry) {
            case ToggleEntry toggle -> {
                requireNoValue(raw);
                set(toggle, toggle.toggled());
            }
            case TextEntry text -> set(text, requireValue(raw).replace('&', '§'));
            case NumberEntry number -> set(number, parseNumber(requireValue(raw)));
        }
        editor.flush();
        notifications.success(describe(entry));
    }

    private void reset(String key) throws CommandSyntaxException {
        SettingEntry<?> entry = lookup(key);
        editor.reset(entry);
        editor.flush();
        notifications.success("%s: сброшено к значению по умолчанию.".formatted(entry.title()));
    }

    private <T> void set(SettingEntry<T> entry, T value) throws CommandSyntaxException {
        Optional<String> problem = editor.apply(entry, value);
        if (problem.isPresent()) {
            throw Cmd.error(problem.get()).create();
        }
    }

    private static String describe(SettingEntry<?> entry) {
        return switch (entry) {
            case ToggleEntry toggle -> "%s %s.".formatted(toggle.title(), toggle.agreement().describe(toggle.value()));
            case TextEntry text -> "%s: %s".formatted(text.title(), text.display());
            case NumberEntry number -> "%s: %d.".formatted(number.title(), number.value());
        };
    }

    private SettingEntry<?> lookup(String key) throws CommandSyntaxException {
        SettingEntry<?> entry = entries.get(key.toLowerCase(Locale.ROOT));
        if (entry == null) {
            throw UNKNOWN_SETTING.create();
        }
        return entry;
    }

    private static String requireValue(String raw) throws CommandSyntaxException {
        if (raw == null) {
            throw VALUE_REQUIRED.create();
        }
        return raw;
    }

    private static void requireNoValue(String raw) throws CommandSyntaxException {
        if (raw != null) {
            throw NO_VALUE_EXPECTED.create();
        }
    }

    private static int parseNumber(String raw) throws CommandSyntaxException {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw Cmd.error("Некорректное число: %s".formatted(raw)).create();
        }
    }

    private void list() {
        chatService.clientMessage(Text.of("%s%sНастройки:".formatted(Colors.AQUA, Colors.BOLD)));
        for (SettingEntry<?> entry : entries.values()) {
            chatService.clientMessage(chatService.suggestTextComponent(
                    "%s%s%s: %s%s".formatted(Colors.GOLD, entry.key(), Colors.WHITE, Colors.GRAY, entry.display()),
                    "%s\nНажмите, чтобы подставить команду изменения".formatted(entry.title()),
                    "/hm settings %s ".formatted(entry.key())));
        }
    }
}
