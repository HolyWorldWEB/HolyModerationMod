package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.zyouime.holymoderation.core.command.CommandErrors;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.CheckoutService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record UnfreezeCommand(CheckoutService checkoutService, String name) implements ModCommand {
    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.executes(exec.of(context -> unfreeze()));
    }

    private void unfreeze() throws CommandSyntaxException {
        if (!checkoutService.isChecking()) {
            throw CommandErrors.NOT_CHECKING.create();
        }
        checkoutService.finish();
    }
}
