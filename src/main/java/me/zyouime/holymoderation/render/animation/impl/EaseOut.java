package me.zyouime.holymoderation.render.animation.impl;

import me.zyouime.holymoderation.render.animation.Animation;

public final class EaseOut extends Animation {

    public EaseOut(float durationTicks) {
        super(durationTicks);
    }

    public EaseOut(float durationTicks, boolean startCompleted) {
        super(durationTicks, startCompleted);
    }

    @Override
    protected double ease(double t) {
        double inverted = 1.0 - t;
        return 1.0 - inverted * inverted * inverted;
    }
}
