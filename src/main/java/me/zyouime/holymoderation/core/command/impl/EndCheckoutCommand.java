package me.zyouime.holymoderation.core.command.impl;

import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import me.zyouime.holymoderation.core.checkout.BanReasonLookup;
import me.zyouime.holymoderation.core.checkout.CheckoutJournal;
import me.zyouime.holymoderation.core.checkout.CheckoutPrompts;
import me.zyouime.holymoderation.core.checkout.CheckoutResult;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.CheckoutService;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record EndCheckoutCommand(CheckoutService checkoutService, CheckoutJournal journal, BanReasonLookup banReasonLookup, CheckoutPrompts prompts) implements ModCommand {

    private static final String RESULT_ARG = "результат";
    private static final String STASH_ARG = "снести_стеш";
    private static final String BAN_REASON_ARG = "причина_бана";
    private static final List<String> STASH_VALUES = List.of("true", "false");
    private static final SimpleCommandExceptionType UNKNOWN_RESULT = Cmd.error("Неизвестный результат проверки.");
    private static final SimpleCommandExceptionType LOOKUP_BUSY = Cmd.error("Причина бана уже запрашивается, подождите.");
    private static final SimpleCommandExceptionType NO_SUSPECT = Cmd.error("Неизвестно, кого вы проверяли. Укажите причину бана вручную: /hm endcheckout ban <снести_стеш> <причина>");

    @Override
    public String name() {
        return "endcheckout";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.choice(RESULT_ARG, CheckoutResult.apiValues())
                .executes(exec.of(context -> end(Cmd.str(context, RESULT_ARG), false, "")))
                .then(Cmd.choice(STASH_ARG, STASH_VALUES)
                        .executes(exec.of(context -> end(
                                Cmd.str(context, RESULT_ARG),
                                Boolean.parseBoolean(Cmd.str(context, STASH_ARG)),
                                "")))
                        .then(Cmd.text(BAN_REASON_ARG)
                                .executes(exec.of(context -> end(
                                        Cmd.str(context, RESULT_ARG),
                                        Boolean.parseBoolean(Cmd.str(context, STASH_ARG)),
                                        Cmd.str(context, BAN_REASON_ARG)))))));
    }

    private void end(String rawResult, boolean destroyStash, String banReason) throws CommandSyntaxException {
        Optional<CheckoutResult> parsed = CheckoutResult.byApiValue(rawResult);
        if (parsed.isEmpty()) {
            throw UNKNOWN_RESULT.create();
        }
        CheckoutResult result = parsed.get();
        if (!result.isNeedsBanReason()) {
            journal.end(result, result.getApiValue(), destroyStash);
            return;
        }
        if (!banReason.isEmpty()) {
            journal.end(result, banReason, destroyStash);
            return;
        }
        String suspect = checkoutService.lastSuspect();
        if (suspect.isEmpty()) {
            throw NO_SUSPECT.create();
        }
        if (banReasonLookup.isActive()) {
            throw LOOKUP_BUSY.create();
        }
        banReasonLookup.request(suspect, reason -> journal.end(result, reason, destroyStash), () -> prompts.showBanReasonPrompt(destroyStash));
    }
}