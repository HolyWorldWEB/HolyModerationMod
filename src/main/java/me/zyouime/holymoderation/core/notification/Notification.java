package me.zyouime.holymoderation.core.notification;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public final class Notification {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1);
    private final long id;
    private final NotificationType type;
    private final String title;
    private final String text;
    private final int lifeTicks;
    private final boolean persistent;
    private State state = State.SPAWNING;
    private int elapsedTicks = 0;

    public Notification(NotificationType type, String title, String text, int lifeTicks, boolean persistent) {
        this.id = ID_SEQUENCE.getAndIncrement();
        this.type = type;
        this.title = title;
        this.text = text;
        this.lifeTicks = lifeTicks;
        this.persistent = persistent;
    }

    public void tick() {
        elapsedTicks++;
    }

    public void state(State state) {
        this.state = state;
        this.elapsedTicks = 0;
    }

    public boolean isExpired() {
        if (persistent) {
            return false;
        }
        return state == State.IDLE && elapsedTicks >= lifeTicks;
    }

    public boolean isHidden(int hideTicks) {
        return state == State.HIDING && elapsedTicks >= hideTicks;
    }

    public boolean isVisible() {
        return state != State.HIDING;
    }

    public float phaseProgress(int phaseTicks) {
        if (phaseTicks <= 0) {
            return 1f;
        }
        return Math.min(1f, (float) elapsedTicks / phaseTicks);
    }

    public enum State {
        SPAWNING,
        IDLE,
        HIDING
    }
}
