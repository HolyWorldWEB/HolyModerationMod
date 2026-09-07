package me.zyouime.holymoderation.core.fabric.events.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;

public interface HandledScreenClickEvent {

    Event<HandledScreenClickEvent> EVENT = EventFactory.createArrayBacked(HandledScreenClickEvent.class, handledScreenClickEvents -> (screen,stack) -> {
        for (HandledScreenClickEvent event : handledScreenClickEvents) {
            event.handleScreenClick(screen, stack);
        }
    });

    void handleScreenClick(HandledScreen<?> screen, ItemStack stack);
}
