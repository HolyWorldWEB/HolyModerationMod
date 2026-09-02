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

public record SendTextsCommand(CheckoutService checkoutService, UserState userState) implements ModCommand {

    @Override
    public String name() {
        return "sendtexts";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.player("игрок")
                .executes(exec.of(context -> sendTexts(Cmd.str(context, "игрок")))));
    }

    private void sendTexts(String player) throws CommandSyntaxException {
        if (userState.isInHub()) {
            throw CommandErrors.IN_HUB.create();
        }
        checkoutService.sendTexts(player);
    }
}
