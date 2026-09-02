package me.zyouime.holymoderation.gui.panel.setting;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.gui.widget.impl.ColorPickerWidget;

import java.awt.Color;

public class ColorSetting extends PanelSetting<Color> {

    private static final float BUTTON_SIZE = 13.0f;
    private final boolean hasAlpha;
    private ColorPickerWidget picker;

    public ColorSetting(Setting<Color> setting, String settingName, boolean hasAlpha) {
        this(DEFAULT_WIDTH, 20.0f, setting, settingName, hasAlpha);
    }

    public ColorSetting(float width, float height, Setting<Color> setting, String settingName, boolean hasAlpha) {
        super(width, height, setting, settingName);
        this.hasAlpha = hasAlpha;
    }

    @Override
    public void init() {
        super.init();
        this.picker = this.addWidget(new ColorPickerWidget(0.0f, 0.0f, BUTTON_SIZE, 100.0f, 60.0f, this.getSetting(), this.hasAlpha));
    }

    @Override
    protected float labelMaxWidth() {
        return this.width - BUTTON_SIZE - 18.0f;
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        super.updatePos(x, y, width, height);
        this.picker.updatePos(x + this.width - BUTTON_SIZE - 6.0f, y + (this.height - BUTTON_SIZE) / 2.0f);
    }
}
