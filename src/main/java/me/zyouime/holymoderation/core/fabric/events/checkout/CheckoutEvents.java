package me.zyouime.holymoderation.core.fabric.events.checkout;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class CheckoutEvents {

    public static final Event<Started> STARTED = EventFactory.createArrayBacked(Started.class,
            callbacks -> suspect -> {
                for (Started callback : callbacks) {
                    callback.onStarted(suspect);
                }
            });

    public static final Event<Finished> FINISHED = EventFactory.createArrayBacked(Finished.class,
            callbacks -> suspect -> {
                for (Finished callback : callbacks) {
                    callback.onFinished(suspect);
                }
            });

    public interface Started {
        void onStarted(String suspect);
    }

    public interface Finished {
        void onFinished(String suspect);
    }
}