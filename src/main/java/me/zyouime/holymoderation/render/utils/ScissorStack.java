package me.zyouime.holymoderation.render.utils;

import com.mojang.blaze3d.systems.RenderPass;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import net.minecraft.client.util.Window;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ScissorStack {

    private static final Deque<Rect> STACK = new ArrayDeque<>();

    private ScissorStack() {
    }

    public static void push(float x1, float y1, float x2, float y2) {
        Rect rect = new Rect(x1, y1, x2, y2);
        Rect parent = STACK.peek();
        STACK.push(parent == null ? rect : parent.intersect(rect));
    }

    public static void pushOverride(float x1, float y1, float x2, float y2) {
        STACK.push(new Rect(x1, y1, x2, y2));
    }

    public static void pop() {
        if (!STACK.isEmpty()) {
            STACK.pop();
        }
    }

    public static void clear() {
        STACK.clear();
    }

    public static Rect current() {
        return STACK.peek();
    }

    public static boolean contains(double x, double y) {
        Rect rect = STACK.peek();
        return rect == null || (x >= rect.x1() && x <= rect.x2() && y >= rect.y1() && y <= rect.y2());
    }

    static void apply(RenderPass renderPass) {
        Rect rect = STACK.peek();
        if (rect == null) {
            return;
        }
        Window window = MinecraftProvider.client().getWindow();
        int scale = window.getScaleFactor();
        if (rect.isEmpty()) {
            renderPass.enableScissor(0, 0, 0, 0);
            return;
        }
        int x = Math.round(rect.x1() * scale);
        int width = Math.round((rect.x2() - rect.x1()) * scale);
        int height = Math.round((rect.y2() - rect.y1()) * scale);
        int y = window.getFramebufferHeight() - Math.round(rect.y2() * scale);
        renderPass.enableScissor(x, y, Math.max(0, width), Math.max(0, height));
    }

    public record Rect(float x1, float y1, float x2, float y2) {

        public boolean isEmpty() {
            return this.x2 <= this.x1 || this.y2 <= this.y1;
        }

        public Rect intersect(Rect other) {
            return new Rect(Math.max(this.x1, other.x1), Math.max(this.y1, other.y1), Math.min(this.x2, other.x2), Math.min(this.y2, other.y2));
        }
    }
}
