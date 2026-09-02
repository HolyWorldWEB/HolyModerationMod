package me.zyouime.holymoderation.gui.panel.setting;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.gui.widget.impl.SliderWidget;
import org.joml.Matrix4fStack;

public class SliderSetting extends PanelSetting<Integer> {

    private static final float TRACK_HEIGHT = 4.0f;
    private static final float SIDE_PADDING = 10.0f;
    private final int min;
    private final int max;
    private SliderWidget<Integer> slider;

    public SliderSetting(Setting<Integer> setting, String settingName, int min, int max) {
        this(DEFAULT_WIDTH, 34.0f, setting, settingName, min, max);
    }

    public SliderSetting(float width, float height, Setting<Integer> setting, String settingName, int min, int max) {
        super(width, height, setting, settingName);
        this.min = min;
        this.max = max;
    }

    @Override
    public void init() {
        super.init();
        this.slider = this.addWidget(SliderWidget.integer(0.0f, 0.0f, 10.0f, TRACK_HEIGHT, this.min, this.max, this.getSetting()));
    }

    @Override
    protected float labelMaxWidth() {
        return this.width - SIDE_PADDING * 2.0f - 26.0f;
    }

    @Override
    protected void renderLabel(Matrix4fStack matrices) {
        if (this.getLabel() == null) {
            return;
        }
        this.getLabel().setMaxWidth(this.labelMaxWidth());
        this.getLabel().render(matrices, this.x + SIDE_PADDING, this.y + 3.0f);
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        super.updatePos(x, y, width, height);
        this.slider.updatePos(x + SIDE_PADDING, y + this.height - TRACK_HEIGHT - 9.0f, this.width - SIDE_PADDING * 2.0f, TRACK_HEIGHT);
    }
}
