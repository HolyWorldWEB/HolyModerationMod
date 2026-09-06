package me.zyouime.holymoderation.core.fabric.events.connection;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerEvents {

    public static final Event<ServerJoinEvent> JOIN = EventFactory.createArrayBacked(ServerJoinEvent.class, callbacks -> address -> {
        for (ServerJoinEvent callback : callbacks) {
            callback.onJoin(address);
        }
    });

    public static final Event<ServerSwitchEvent> SWITCH = EventFactory.createArrayBacked(ServerSwitchEvent.class, callbacks -> () -> {
        for (ServerSwitchEvent callback : callbacks) {
            callback.onSwitch();
        }
    });

    public static final Event<ServerLeaveEvent> LEAVE = EventFactory.createArrayBacked(ServerLeaveEvent.class, callbacks -> () -> {
        for (ServerLeaveEvent callback : callbacks) {
            callback.onLeave();
        }
    });

    public interface ServerSwitchEvent {
        void onSwitch();
    }

    public interface ServerLeaveEvent {
        void onLeave();
    }

    public interface ServerJoinEvent {
        void onJoin(String address);
    }
}
