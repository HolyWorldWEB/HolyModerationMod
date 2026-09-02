package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.CommandErrors;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.states.UserState;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record FreezeCommand(CheckoutService checkoutService, UserState userState, String name) implements ModCommand {

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.player("игрок").executes(exec.of(context -> freeze(Cmd.str(context, "игрок")))));
    }

    private void freeze(String player) throws CommandSyntaxException {
        if (userState.isInHub()) {
            throw CommandErrors.IN_HUB.create();
        }
        checkoutService.start(player);
    }
}
