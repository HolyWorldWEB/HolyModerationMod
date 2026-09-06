package me.zyouime.holymoderation.core.module.impl;

import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.Module;

import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.core.service.NotificationsService;

@RequiredArgsConstructor
public final class NotificationsModule extends Module {

    private final NotificationsService notificationsService;

    @Override
    public void init() {
        ServerEvents.LEAVE.register(notificationsService::clear);
    }

    @Override
    public void tick() {
        notificationsService.tick();
    }

    @Override
    public boolean canDisable() {
        return false;
    }
}
