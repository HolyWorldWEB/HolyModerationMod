package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Optional;

import me.zyouime.holymoderation.core.checkout.CheckoutJournal;
import me.zyouime.holymoderation.core.checkout.CheckoutReason;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public record StartCheckoutCommand(CheckoutJournal journal) implements ModCommand {

    private static final SimpleCommandExceptionType UNKNOWN_REASON = Cmd.error("Неизвестная причина проверки.");

    @Override
    public String name() {
        return "startcheckout";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.player("игрок")
                .then(Cmd.choice("причина", CheckoutReason.apiValues())
                        .executes(exec.of(context -> start(
                                Cmd.str(context, "игрок"),
                                Cmd.str(context, "причина"))))));
    }

    private void start(String player, String rawReason) throws CommandSyntaxException {
        Optional<CheckoutReason> reason = CheckoutReason.byApiValue(rawReason);
        if (reason.isEmpty()) {
            throw UNKNOWN_REASON.create();
        }
        journal.start(player, reason.get());
    }
}
