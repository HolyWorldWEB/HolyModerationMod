package me.zyouime.holymoderation.core.module.impl;

import java.util.List;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.command.impl.SpyFreezeCommand;
import me.zyouime.holymoderation.core.fabric.events.chat.MessageEvent;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.spy.SpyChatListener;
import me.zyouime.holymoderation.core.command.impl.SpyCommand;
import me.zyouime.holymoderation.core.service.SpyService;
import me.zyouime.holymoderation.core.states.UserState;

import net.minecraft.util.ActionResult;

public final class SpyModule extends Module {

    private final SpyService service;
    private final SpyChatListener chatListener;
    private final List<ModCommand> commands;

    public SpyModule(SpyService service, UserState userState, CheckoutService checkoutService, ChatService chatService, ModSettings settings, LoggerService logger) {
        this.service = service;
        this.chatListener = new SpyChatListener(service, userState, chatService, settings, logger);
        this.commands = List.of(new SpyCommand(service, checkoutService, userState), new SpyFreezeCommand(service, checkoutService, userState));
    }

    @Override
    public void init() {
        MessageEvent.EVENT.register(message -> {
            if (!isEnabled()) {
                return ActionResult.PASS;
            }
            return chatListener.onMessage(message);
        });
        ServerEvents.SWITCH.register(() -> {
            if (!isEnabled()) {
                return;
            }
            service.onServerSwitch();
        });
        ServerEvents.LEAVE.register(service::endSpy);
    }

    @Override
    public List<ModCommand> commands() {
        return commands;
    }

    @Override
    public void tick() {
        service.tick();
    }

    @Override
    protected void onDisable() {
        service.endSpy();
    }
}
