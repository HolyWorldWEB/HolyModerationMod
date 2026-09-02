package me.zyouime.holymoderation.core.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.List;

public record CommandInitializer(List<ModCommand> commands, LoggerService logger, NotificationsService notificationsService) {

    public static final String ROOT = "hm";

    public void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal(ROOT);
            for (ModCommand command : commands) {
                LiteralArgumentBuilder<FabricClientCommandSource> node = ClientCommandManager.literal(command.name());
                command.configure(node, new Exec(logger, notificationsService, command.name()));
                root.then(node);
            }
            dispatcher.register(root);
        });
    }
}