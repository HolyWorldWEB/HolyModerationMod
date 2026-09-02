package me.zyouime.holymoderation.core.fabric.events.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface CommandSendEvent {
    Event<CommandSendEvent> EVENT = EventFactory.createArrayBacked(CommandSendEvent.class, callbacks -> command -> {
        for (CommandSendEvent callback : callbacks) {
            ActionResult result = callback.onCommand(command);
            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult onCommand(String command);
}
