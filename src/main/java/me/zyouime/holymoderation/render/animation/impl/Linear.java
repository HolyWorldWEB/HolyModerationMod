package me.zyouime.holymoderation.render.animation.impl;

import me.zyouime.holymoderation.render.animation.Animation;

public final class Linear extends Animation {

    public Linear(float durationTicks) {
        super(durationTicks);
    }

    public Linear(float durationTicks, boolean startCompleted) {
        super(durationTicks, startCompleted);
    }

    @Override
    protected double ease(double t) {
        return t;
    }
}
