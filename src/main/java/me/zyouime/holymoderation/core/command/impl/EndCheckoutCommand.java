package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.Optional;

import me.zyouime.holymoderation.core.checkout.BanReasonLookup;
import me.zyouime.holymoderation.core.checkout.CheckoutJournal;
import me.zyouime.holymoderation.core.checkout.CheckoutPrompts;
import me.zyouime.holymoderation.core.checkout.CheckoutResult;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record EndCheckoutCommand(CheckoutJournal journal, BanReasonLookup banReasonLookup, CheckoutPrompts prompts) implements ModCommand {

    private static final List<String> STASH_VALUES = List.of("true", "false");
    private static final SimpleCommandExceptionType UNKNOWN_RESULT = Cmd.error("Неизвестный результат проверки.");
    private static final SimpleCommandExceptionType LOOKUP_BUSY = Cmd.error("Причина бана уже запрашивается, подождите.");

    @Override
    public String name() {
        return "endcheckout";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.choice("результат", CheckoutResult.apiValues())
                .executes(exec.of(context -> end(
                        Cmd.str(context, "результат"), "", false, "")))
                .then(Cmd.player("игрок")
                        .then(Cmd.choice("снести_стеш", STASH_VALUES)
                                .executes(exec.of(context -> end(
                                        Cmd.str(context, "результат"),
                                        Cmd.str(context, "игрок"),
                                        Boolean.parseBoolean(Cmd.str(context, "снести_стеш")),
                                        "")))
                                .then(Cmd.text("причина_бана")
                                        .executes(exec.of(context -> end(
                                                Cmd.str(context, "результат"),
                                                Cmd.str(context, "игрок"),
                                                Boolean.parseBoolean(Cmd.str(context, "снести_стеш")),
                                                Cmd.str(context, "причина_бана"))))))));
    }

    private void end(String rawResult, String player, boolean destroyStash, String banReason) throws CommandSyntaxException {
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
        if (banReasonLookup.isActive()) {
            throw LOOKUP_BUSY.create();
        }
        banReasonLookup.request(player, reason -> journal.end(result, reason, destroyStash), () -> prompts.showBanReasonPrompt(player, destroyStash));
    }
}
