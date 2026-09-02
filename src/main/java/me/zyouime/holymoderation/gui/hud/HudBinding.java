package me.zyouime.holymoderation.gui.hud;

import me.zyouime.holymoderation.config.setting.Setting;

public record HudBinding(Setting<Boolean> enabled, Setting<Integer> scale, Setting<Float> x, Setting<Float> y) {
}