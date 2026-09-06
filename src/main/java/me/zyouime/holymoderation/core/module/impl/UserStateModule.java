package me.zyouime.holymoderation.core.module.impl;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.fabric.events.chat.CommandSendEvent;
import me.zyouime.holymoderation.core.fabric.events.chat.MessageEvent;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.states.UserState;
import me.zyouime.holymoderation.core.user.*;
import net.minecraft.util.ActionResult;

import java.util.List;

public final class UserStateModule extends Module {

    private final UserState userState;
    private final UserLocator locator;
    private final UserStateListener listener;
    private final SelfStateTracker selfStateTracker;
    private final AutoCommands autoCommands;

    public UserStateModule(UserState userState, ChatService chatService, NotificationsService notifications, ModSettings settings, LoggerService logger, VanishController vanishController) {
        this.userState = userState;
        this.locator = new UserLocator(userState, chatService);
        this.listener = new UserStateListener(userState, locator, chatService, logger);
        this.selfStateTracker = new SelfStateTracker(userState, notifications, vanishController);
        this.autoCommands = new AutoCommands(userState, chatService, settings, vanishController);
    }

    @Override
    public void init() {
        MessageEvent.EVENT.register(message -> {
            if (!isEnabled()) {
                return ActionResult.PASS;
            }
            return listener.onMessage(message);
        });
        CommandSendEvent.EVENT.register(command -> {
            if (!isEnabled()) {
                return ActionResult.PASS;
            }
            selfStateTracker.onCommand(command);
            return ActionResult.PASS;
        });
        ServerEvents.JOIN.register(address -> onWorldEnter(false));
        ServerEvents.SWITCH.register(() -> onWorldEnter(true));
        ServerEvents.LEAVE.register(locator::stop);
    }

    @Override
    public void tick() {
        locator.tick();
    }

    @Override
    protected void onDisable() {
        if (this.locator.isSearching()) {
            this.locator.stop();
        }
    }

    private void onWorldEnter(boolean isSwitch) {
        if (!isEnabled()) {
            return;
        }
        userState.setGameInitCompleted(false);
        if (isSwitch && !userState.isInHub()) {
            autoCommands.applyAfterSwitch();
        }
        userState.setGameInitCompleted(true);
        if (userState.isInHub()) {
            locator.stop();
            return;
        }
        locator.start();
    }
}