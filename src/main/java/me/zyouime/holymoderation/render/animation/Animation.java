package me.zyouime.holymoderation.render.animation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;

public abstract class Animation {

    private static final float MAX_FRAME_DELTA = 0.1f;
    @Getter
    @Setter
    private static boolean enabled = true;
    private static long lastFrameNanos = -1L;
    private static float frameDelta = 1.0f / 60.0f;

    private final float durationSeconds;
    private double progress;

    protected Animation(float durationTicks) {
        this(durationTicks, false);
    }

    protected Animation(float durationTicks, boolean startCompleted) {
        this.durationSeconds = Math.max(0.001f, durationTicks / 20.0f);
        this.progress = startCompleted ? 1.0 : 0.0;
    }

    public static void tick() {
        long now = System.nanoTime();
        if (lastFrameNanos < 0L) {
            lastFrameNanos = now;
            return;
        }
        frameDelta = MathHelper.clamp((now - lastFrameNanos) / 1000000000f, 0.0f, MAX_FRAME_DELTA);
        lastFrameNanos = now;
    }

    public static float frameDelta() {
        return frameDelta;
    }

    public static double fast(double current, double target, double speed) {
        if (!enabled) {
            return target;
        }
        double diff = target - current;
        if (Math.abs(diff) < 0.01) {
            return target;
        }
        return current + diff * MathHelper.clamp(speed * frameDelta, 0.0, 1.0);
    }

    public void update() {
        this.update(true);
    }

    public void update(boolean forward) {
        if (!enabled) {
            this.progress = forward ? 1.0 : 0.0;
            return;
        }
        double step = frameDelta / this.durationSeconds;
        this.progress = MathHelper.clamp(this.progress + (forward ? step : -step), 0.0, 1.0);
    }

    public double getAnimationD() {
        return this.ease(this.progress);
    }

    public float getAnimation() {
        return (float) this.getAnimationD();
    }

    public boolean isFinished() {
        return this.progress >= 1.0;
    }

    public boolean isReset() {
        return this.progress <= 0.0;
    }

    public void reset() {
        this.progress = 0.0;
    }

    public void complete() {
        this.progress = 1.0;
    }

    protected abstract double ease(double t);
}
