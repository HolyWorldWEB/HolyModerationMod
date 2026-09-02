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
import me.zyouime.holymoderation.core.spy.SpySession;
import me.zyouime.holymoderation.core.states.UserState;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record SpyFreezeCommand(SpyService spyService, CheckoutService checkoutService, UserState userState) implements ModCommand {

    private static final SimpleCommandExceptionType ALREADY_CHECKING = Cmd.error("Вы уже проверяете игрока. Сначала закончите текущую проверку: /hm unfrz");

    @Override
    public String name() {
        return "spyfrz";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.executes(exec.of(context -> freeze()));
    }

    private void freeze() throws CommandSyntaxException {
        if (userState.isInHub()) {
            throw CommandErrors.IN_HUB.create();
        }
        if (checkoutService.isChecking()) {
            throw ALREADY_CHECKING.create();
        }
        SpySession session = spyService.sessionOrNull();
        if (session == null) {
            throw CommandErrors.NOT_SPYING.create();
        }
        checkoutService.start(session.getPlayer());
        spyService.endSpy();
    }
}
