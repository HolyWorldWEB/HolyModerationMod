package me.zyouime.holymoderation.core.module.impl;

import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.core.fabric.events.screen.HandledScreenClickEvent;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ReportService;

@RequiredArgsConstructor
public final class ReportModule extends Module {

    private final ReportService reportService;

    @Override
    public void init() {
        HandledScreenClickEvent.EVENT.register((screen, clickEvent) -> {
            if (!isEnabled()) {
                return;
            }
            reportService.handleScreenClick(screen, clickEvent);
        });
    }
}
