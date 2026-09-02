package me.zyouime.holymoderation.gui.panel.setting;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.gui.widget.impl.ToggleWidget;

public class BooleanSetting extends PanelSetting<Boolean> {

    private static final float TOGGLE_WIDTH = 22.0f;
    private static final float TOGGLE_HEIGHT = 12.0f;
    private ToggleWidget toggle;

    public BooleanSetting(Setting<Boolean> setting, String settingName) {
        this(DEFAULT_WIDTH, 23.0f, setting, settingName);
    }

    public BooleanSetting(float width, float height, Setting<Boolean> setting, String settingName) {
        super(width, height, setting, settingName);
    }

    @Override
    public void init() {
        super.init();
        this.toggle = this.addWidget(new ToggleWidget(0.0f, 0.0f, TOGGLE_WIDTH, TOGGLE_HEIGHT, this.getSetting()));
    }

    @Override
    protected float labelMaxWidth() {
        return this.width - TOGGLE_WIDTH - 28.0f;
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        super.updatePos(x, y, width, height);
        this.toggle.updatePos(x + this.width - TOGGLE_WIDTH - 10.0f, y + (this.height - TOGGLE_HEIGHT) / 2.0f);
    }
}
