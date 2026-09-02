package me.zyouime.holymoderation.core.module.impl;

import java.util.List;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.command.impl.ProfileCommand;
import me.zyouime.holymoderation.core.command.impl.StatsCommand;
import me.zyouime.holymoderation.core.moderator.ModeratorPrinter;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.ModeratorService;
import me.zyouime.holymoderation.core.states.ModeratorState;

public final class ModeratorModule extends Module {

    private final ModeratorService service;
    private final List<ModCommand> commands;

    public ModeratorModule(ModeratorState state, ModeratorService service, ChatService chatService) {
        this.service = service;
        ModeratorPrinter printer = new ModeratorPrinter(state, chatService);
        this.commands = List.of(new ProfileCommand(service, printer), new StatsCommand(service, printer));
    }

    @Override
    public void init() {
        service.refreshAll();
    }

    @Override
    public List<ModCommand> commands() {
        return commands;
    }
}
