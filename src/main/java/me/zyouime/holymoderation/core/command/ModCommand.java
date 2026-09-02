package me.zyouime.holymoderation.core.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface ModCommand {

    String name();

    void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec);
}
