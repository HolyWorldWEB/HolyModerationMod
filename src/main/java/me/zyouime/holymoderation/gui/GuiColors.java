package me.zyouime.holymoderation.gui;

import java.awt.Color;

public final class GuiColors {

    public static final Color BACKDROP = new Color(10, 7, 5, 170);
    public static final Color CARD = new Color(26, 21, 17, 250);
    public static final Color CARD_HEADER = new Color(32, 25, 20, 255);
    public static final Color CATEGORY = new Color(36, 28, 22, 255);
    public static final Color CATEGORY_HEADER = new Color(44, 35, 27, 255);
    public static final Color ELEMENT_BACK = new Color(21, 16, 12, 255);
    public static final Color ROW_HOVER = new Color(255, 232, 214, 14);
    public static final Color DIVIDER = new Color(36, 29, 23, 255);
    public static final Color BORDER = new Color(51, 41, 31, 255);
    public static final Color BORDER_FOCUS = new Color(255, 138, 61, 255);
    public static final Color TRACK = new Color(58, 47, 38, 255);
    public static final Color ACCENT = new Color(255, 138, 61);
    public static final Color ACCENT_HOVER = new Color(255, 178, 122);
    public static final Color ACCENT_DIM = new Color(255, 138, 61, 90);
    public static final Color ACCENT_BACK = new Color(58, 33, 19, 255);
    public static final Color ON_ACCENT = new Color(74, 27, 12, 255);
    public static final Color TEXT = new Color(245, 237, 229);
    public static final Color TEXT_MUTED = new Color(184, 168, 153);
    public static final Color TEXT_FAINT = new Color(126, 112, 101);
    public static final Color TEXT_DISABLED = new Color(78, 68, 59);
    public static final Color KNOB = new Color(250, 243, 235);
    public static final Color KNOB_OFF = new Color(138, 122, 107);
    public static final Color DANGER = new Color(226, 88, 80);

    private GuiColors() {
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color mix(Color from, Color to, float progress) {
        float t = Math.max(0.0f, Math.min(1.0f, progress));
        return new Color(
                Math.round(from.getRed() + (to.getRed() - from.getRed()) * t),
                Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * t),
                Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * t),
                Math.round(from.getAlpha() + (to.getAlpha() - from.getAlpha()) * t));
    }
}
