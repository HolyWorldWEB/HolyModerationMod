package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.moderator.ModeratorPrinter;
import me.zyouime.holymoderation.core.service.ModeratorService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record ProfileCommand(ModeratorService moderatorService, ModeratorPrinter printer) implements ModCommand {
    @Override
    public String name() {
        return "profile";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.executes(exec.of(context -> show()));
    }

    private void show() {
        printer.printProfile();
        moderatorService.refreshProfile(true);
    }
}
