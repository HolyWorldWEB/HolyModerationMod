package me.zyouime.holymoderation.gui.panel.setting;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.gui.widget.impl.SimpleTextFieldWidget;
import org.joml.Matrix4fStack;

public class TextSetting extends PanelSetting<String> {

    private static final float FIELD_HEIGHT = 16.0f;
    private static final float SIDE_PADDING = 10.0f;
    private static final char SECTION = '§';
    private String placeholder = "";
    private boolean colorCodes;
    private int maxLength = 256;
    private SimpleTextFieldWidget field;

    public TextSetting(Setting<String> setting, String settingName) {
        this(DEFAULT_WIDTH, 38.0f, setting, settingName);
    }

    public TextSetting(float width, float height, Setting<String> setting, String settingName) {
        super(width, height, setting, settingName);
    }

    public TextSetting placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public TextSetting colorCodes(boolean colorCodes) {
        this.colorCodes = colorCodes;
        return this;
    }

    public TextSetting maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    private String toDisplay(String value) {
        if (value == null) {
            return "";
        }
        return this.colorCodes ? value.replace(SECTION, '&') : value;
    }

    private String toStored(String value) {
        return this.colorCodes ? value.replace('&', SECTION) : value;
    }

    @Override
    public void init() {
        super.init();
        this.field = new SimpleTextFieldWidget(0.0f, 0.0f, this.width - SIDE_PADDING * 2.0f, FIELD_HEIGHT, null);
        this.field.setPlaceholder(this.placeholder);
        this.field.setMaxLength(this.maxLength);
        this.field.setText(this.toDisplay(this.getSetting() == null ? "" : this.getSetting().getValue()));
        this.field.setCallback(text -> {
            if (this.getSetting() != null) {
                this.getSetting().setValue(this.toStored(text));
            }
        });
        this.addWidget(this.field);
    }

    @Override
    public void resetSetting() {
        if (this.getSetting() == null) {
            return;
        }
        this.getSetting().reset();
        this.field.setText(this.toDisplay(this.getSetting().getValue()));
    }

    @Override
    protected float labelMaxWidth() {
        return this.width - SIDE_PADDING * 2.0f;
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
        this.field.updatePos(x + SIDE_PADDING, y + this.height - FIELD_HEIGHT - 5.0f, this.width - SIDE_PADDING * 2.0f, FIELD_HEIGHT);
    }
}
