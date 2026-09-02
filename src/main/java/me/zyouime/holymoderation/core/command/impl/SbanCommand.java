package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.CommandErrors;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.punishment.PunishmentType;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.service.PunishmentService;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record SbanCommand(PunishmentService punishmentService, CheckoutService checkoutService) implements ModCommand {

    private static final String RULE = "2.4";
    private static final String TIME_ARG = "время";
    private static final String REASON_ARG = "причина";

    @Override
    public String name() {
        return "sban";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.word(TIME_ARG)
                .executes(exec.of(context -> ban(Cmd.str(context, TIME_ARG), null)))
                .then(Cmd.text(REASON_ARG)
                        .executes(exec.of(context ->
                                ban(Cmd.str(context, TIME_ARG), Cmd.str(context, REASON_ARG))))));
    }

    private void ban(String time, String comment) throws CommandSyntaxException {
        String suspect = checkoutService.suspect();
        if (suspect.isEmpty()) {
            throw CommandErrors.NOT_CHECKING.create();
        }
        if (!punishmentService.punish(PunishmentType.BANIP, suspect, time, reason(comment), true)) {
            return;
        }
        checkoutService.finishAfterBan();
    }

    private static String reason(String comment) {
        if (comment == null || comment.isBlank()) {
            return RULE;
        }
        return "%s (%s)".formatted(RULE, comment.trim());
    }
}
