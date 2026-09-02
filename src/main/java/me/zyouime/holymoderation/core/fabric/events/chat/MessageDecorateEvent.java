package me.zyouime.holymoderation.core.fabric.events.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

public interface MessageDecorateEvent {

    Event<MessageDecorateEvent> EVENT = EventFactory.createArrayBacked(MessageDecorateEvent.class, callbacks -> message -> {
        Text current = message;
        for (MessageDecorateEvent callback : callbacks) {
            current = callback.decorate(current);
        }
        return current;
    });

    Text decorate(Text message);
}