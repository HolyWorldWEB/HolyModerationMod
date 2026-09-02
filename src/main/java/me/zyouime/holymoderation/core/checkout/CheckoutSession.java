package me.zyouime.holymoderation.core.checkout;

import lombok.Getter;

@Getter
public final class CheckoutSession {

    private final String suspect;
    private int ticksSinceStart = 0;
    private boolean awaitingFreeze = true;

    public CheckoutSession(String suspect) {
        this.suspect = suspect;
    }

    public void tick() {
        ticksSinceStart++;
    }

    public void freezeAnswered() {
        awaitingFreeze = false;
    }

    public boolean isAtTick(int tick) {
        return ticksSinceStart == tick;
    }

    public boolean waitedLongerThan(int ticks) {
        return awaitingFreeze && ticksSinceStart > ticks;
    }
}
