package me.zyouime.holymoderation.core.module.impl;

import me.zyouime.holymoderation.core.fabric.events.checkout.CheckoutEvents;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ObsService;

public final class ObsModule extends Module {

    private final ObsService service;

    public ObsModule(ObsService service) {
        this.service = service;
    }

    @Override
    public void init() {
        CheckoutEvents.STARTED.register(suspect -> {
            if (isEnabled()) {
                service.startRecording(suspect);
            }
        });
        CheckoutEvents.FINISHED.register(suspect -> {
            if (isEnabled()) {
                service.stopRecording();
            }
        });
        ServerEvents.LEAVE.register(service::disconnect);
    }

    @Override
    protected void onDisable() {
        service.disconnect();
    }
}