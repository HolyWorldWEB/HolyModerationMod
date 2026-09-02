package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.NotificationsService;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public record SetVkCommand(ModSettings settings, NotificationsService notifications) implements ModCommand {

    @Override
    public String name() {
        return "setvk";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.then(Cmd.text("ссылка")
                .executes(exec.of(context -> setLink(Cmd.str(context, "ссылка")))));
    }

    private void setLink(String link) {
        settings.vkLink.setValue(link);
        settings.saveSettings();
        notifications.success("Ссылка на ВК сохранена: %s".formatted(link));
    }
}
