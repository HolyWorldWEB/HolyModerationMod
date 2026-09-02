package me.zyouime.holymoderation.core.module.impl;

import java.util.List;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.command.impl.SettingsCommand;
import me.zyouime.holymoderation.core.command.impl.TextsCommand;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.settings.SettingsCatalog;
import me.zyouime.holymoderation.core.settings.SettingsEditor;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.NotificationsService;

public final class SettingsModule extends Module {

    private final List<ModCommand> commands;

    public SettingsModule(ModSettings settings, SettingsEditor editor, ChatService chatService, NotificationsService notifications) {
        this.commands = List.of(new SettingsCommand(editor, SettingsCatalog.of(settings), chatService, notifications), new TextsCommand(settings, chatService, notifications));
    }

    @Override
    public void init() {
    }

    @Override
    public List<ModCommand> commands() {
        return commands;
    }
}
