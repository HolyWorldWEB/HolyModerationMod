package me.zyouime.holymoderation.gui.hud;

import java.util.List;

import org.joml.Matrix4fStack;

public final class Huds {

    public static final SpyHud SPY = new SpyHud();
    public static final CheckoutHud CHECKOUT = new CheckoutHud();
    private static final List<HudPanel> ALL = List.of(SPY, CHECKOUT);

    private Huds() {
    }

    public static void render(Matrix4fStack matrices) {
        for (HudPanel panel : ALL) {
            panel.render(matrices);
        }
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (HudPanel panel : ALL) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public static boolean mouseDragged(double mouseX, double mouseY, int button) {
        for (HudPanel panel : ALL) {
            if (panel.mouseDragged(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public static boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (HudPanel panel : ALL) {
            if (panel.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    public static void reset() {
        ALL.forEach(HudPanel::reset);
    }

    public static void resetPositions() {
        ALL.forEach(HudPanel::resetPosition);
    }
}