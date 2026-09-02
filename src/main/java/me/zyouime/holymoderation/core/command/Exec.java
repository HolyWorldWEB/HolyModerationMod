package me.zyouime.holymoderation.core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.util.Colors;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record Exec(LoggerService logger, NotificationsService notificationsService, String commandName) {

    public Command<FabricClientCommandSource> of(Action action) {
        return context -> {
            try {
                action.run(context);
            } catch (CommandSyntaxException e) {
                reply(e.getMessage());
                return 0;
            }catch (Exception e) {
                logger.exception("Команда '%s': %s".formatted(commandName, e));
                reply("Внутренняя ошибка, подробности в логе");
                return 0;
            }
            return Command.SINGLE_SUCCESS;
        };
    }

    private void reply(String message) {
        notificationsService.error("%s%s".formatted(Colors.RED, message));
    }

    public interface Action {
        void run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException;
    }
}