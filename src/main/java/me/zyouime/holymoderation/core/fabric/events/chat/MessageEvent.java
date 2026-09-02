package me.zyouime.holymoderation.core.fabric.events.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface MessageEvent {

    Event<MessageEvent> EVENT = EventFactory.createArrayBacked(MessageEvent.class, callbacks -> (message) -> {
        for (MessageEvent callback : callbacks) {
            ActionResult actionResult = callback.onMessage(message);
            if (actionResult != ActionResult.PASS) {
                return actionResult;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult onMessage(Text text);
}
