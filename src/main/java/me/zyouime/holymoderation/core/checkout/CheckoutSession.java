package me.zyouime.holymoderation.core.checkout;

import lombok.Getter;

@Getter
public final class CheckoutSession {

    private final String suspect;
    private int ticksAwaitingFreeze = 0;
    private int ticksSinceStart = 0;
    private boolean awaitingFreeze = true;
    private boolean started = false;

    public CheckoutSession(String suspect) {
        this.suspect = suspect;
    }

    public void tick() {
        if (awaitingFreeze) {
            ticksAwaitingFreeze++;
            return;
        }
        ticksSinceStart++;
    }

    public void freezeAnswered() {
        awaitingFreeze = false;
    }

    public void markStarted() {
        started = true;
    }

    public boolean isAtTick(int tick) {
        return started && ticksSinceStart == tick;
    }

    public boolean waitedLongerThan(int ticks) {
        return awaitingFreeze && ticksAwaitingFreeze > ticks;
    }
}