package me.zyouime.holymoderation.core.module.impl;

import java.util.List;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.command.impl.SetVkCommand;
import me.zyouime.holymoderation.core.fabric.events.chat.CommandSendEvent;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.punishment.PunishmentCommandListener;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.service.PunishmentService;
import net.minecraft.util.ActionResult;

public final class PunishmentModule extends Module {

    private final PunishmentCommandListener listener;
    private final List<ModCommand> commands;

    public PunishmentModule(PunishmentService punishmentService, CheckoutService checkoutService, NotificationsService notifications, ModSettings settings) {
        this.listener = new PunishmentCommandListener(punishmentService, checkoutService, notifications);
        this.commands = List.of(new SetVkCommand(settings, notifications));
    }

    @Override
    public void init() {
        CommandSendEvent.EVENT.register(command -> {
            if (!isEnabled()) {
                return ActionResult.PASS;
            }
            return listener.onCommand(command);
        });
        ServerEvents.LEAVE.register(listener::reset);
    }

    @Override
    public List<ModCommand> commands() {
        return commands;
    }

    @Override
    protected void onDisable() {
        listener.reset();
    }
}
