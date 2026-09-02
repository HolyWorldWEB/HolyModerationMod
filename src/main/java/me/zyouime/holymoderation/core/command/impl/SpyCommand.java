package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.CommandErrors;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.service.SpyService;
import me.zyouime.holymoderation.core.states.UserState;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record SpyCommand(SpyService spyService, CheckoutService checkoutService, UserState userState) implements ModCommand {

    private static final SimpleCommandExceptionType ON_CHECKOUT = Cmd.error("Вы не можете начать следить за игроком на вашей проверке.");
    private static final SimpleCommandExceptionType ALREADY_SPYING = Cmd.error("Вы уже следите за кем-то. Остановить: /hm spy");
    private static final SimpleCommandExceptionType SELF_SPY = Cmd.error("Нельзя следить за собой.");

    @Override
    public String name() {
        return "spy";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.executes(exec.of(context -> stop()))
                .then(Cmd.player("игрок")
                        .executes(exec.of(context -> start(Cmd.str(context, "игрок")))));
    }

    private void stop() throws CommandSyntaxException {
        if (!spyService.isSpying()) {
            throw CommandErrors.NOT_SPYING.create();
        }
        spyService.endSpy();
    }

    private void start(String target) throws CommandSyntaxException {
        if (userState.isInHub()) {
            throw CommandErrors.IN_HUB.create();
        }
        String checkoutPlayer = checkoutService.suspect();
        if (checkoutPlayer != null && !checkoutPlayer.isEmpty() && checkoutPlayer.equalsIgnoreCase(target)) {
            throw ON_CHECKOUT.create();
        }
        if (spyService.isSpying()) {
            throw ALREADY_SPYING.create();
        }
        if (target.equalsIgnoreCase(userState.getUserNickname())) {
            throw SELF_SPY.create();
        }
        spyService.startSpy(target);
    }
}
