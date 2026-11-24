package me.zyouime.holymoderation.render.animation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;

public abstract class Animation {

    @Getter
    @Setter
    private static boolean enabled = true;
    private static long frameNanos = System.nanoTime();
    private static float frameDelta = 1.0f / 60.0f;

    private final float durationSeconds;
    private double progress;
    private long updatedAt = System.nanoTime();

    protected Animation(float durationTicks) {
        this(durationTicks, false);
    }

    protected Animation(float durationTicks, boolean startCompleted) {
        this.durationSeconds = Math.max(0.001f, durationTicks / 20.0f);
        this.progress = startCompleted ? 1.0 : 0.0;
    }

    public static void beginFrame(long now) {
        frameNanos = now;
    }

    public static long frameNanos() {
        return frameNanos;
    }

    public static void useLayerDelta(long previousFrame) {
        frameDelta = previousFrame == 0L ? 1.0f / 60.0f : elapsedSeconds(previousFrame, frameNanos);
    }

    private static float elapsedSeconds(long start, long end) {
        return Math.max(0L, end - start) / 1_000_000_000.0f;
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
        return current + diff * (1.0 - Math.exp(-Math.max(0.0, speed) * frameDelta));
    }

    public void update() {
        this.update(true);
    }

    public void update(boolean forward) {
        float elapsed = elapsedSeconds(this.updatedAt, frameNanos);
        if (elapsed > 0.0f) this.updatedAt = frameNanos;
        if (!enabled) {
            this.progress = forward ? 1.0 : 0.0;
            return;
        }
        double step = elapsed / this.durationSeconds;
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
        this.updatedAt = System.nanoTime();
    }

    public void complete() {
        this.progress = 1.0;
        this.updatedAt = System.nanoTime();
    }

    protected abstract double ease(double t);
}
