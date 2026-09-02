package me.zyouime.holymoderation.gui.hud;

import java.awt.Color;

public record HudLine(String text, Color dot, Color color) {

    public static HudLine of(String text, Color color) {
        return new HudLine(text, null, color);
    }

    public static HudLine dotted(String text, Color dot, Color color) {
        return new HudLine(text, dot, color);
    }
}