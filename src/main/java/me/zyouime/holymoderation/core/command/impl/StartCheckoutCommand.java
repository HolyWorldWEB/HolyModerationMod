package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Optional;

import me.zyouime.holymoderation.core.checkout.CheckoutJournal;
import me.zyouime.holymoderation.core.checkout.CheckoutReason;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.CommandErrors;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.CheckoutService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public record StartCheckoutCommand(CheckoutService checkoutService, CheckoutJournal journal) implements ModCommand {

    private static final String CHECKOUT_REASON_ARG = "причина_проверки";
    private static final SimpleCommandExceptionType UNKNOWN_REASON = Cmd.error("Неизвестная причина проверки.");

    @Override
    public String name() {
        return "startcheckout";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.choice(CHECKOUT_REASON_ARG, CheckoutReason.apiValues())
                .executes(exec.of(context -> start(Cmd.str(context, CHECKOUT_REASON_ARG)))));
    }

    private void start(String rawReason) throws CommandSyntaxException {
        Optional<CheckoutReason> reason = CheckoutReason.byApiValue(rawReason);
        if (reason.isEmpty()) {
            throw UNKNOWN_REASON.create();
        }
        if (!checkoutService.isChecking()) {
            throw CommandErrors.NOT_CHECKING.create();
        }
        journal.start(checkoutService.suspect(), reason.get());
    }
}
