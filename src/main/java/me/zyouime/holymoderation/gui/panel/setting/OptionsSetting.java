package me.zyouime.holymoderation.gui.panel.setting;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.gui.widget.impl.OptionsListWidget;
import me.zyouime.holymoderation.gui.widget.impl.option.AbstractOptionEntry;

import java.util.ArrayList;
import java.util.List;

public class OptionsSetting<T> extends PanelSetting<T> {

    private static final float LIST_WIDTH = 72.0f;
    private static final float ENTRY_HEIGHT = 14.0f;
    private static final float MAX_DROPDOWN = 70.0f;
    private final List<AbstractOptionEntry<T>> options = new ArrayList<>();
    private final T initialValue;
    private OptionsListWidget<T> list;

    public OptionsSetting(Setting<T> setting, String settingName, List<AbstractOptionEntry<T>> options, T initialValue) {
        this(DEFAULT_WIDTH, 20.0f, setting, settingName, options, initialValue);
    }

    public OptionsSetting(float width, float height, Setting<T> setting, String settingName, List<AbstractOptionEntry<T>> options, T initialValue) {
        super(width, height, setting, settingName);
        this.options.addAll(options);
        this.initialValue = initialValue;
    }

    @Override
    public void init() {
        super.init();
        this.list = new OptionsListWidget<>(0.0f, 0.0f, LIST_WIDTH, ENTRY_HEIGHT, MAX_DROPDOWN, 0.0f);
        for (AbstractOptionEntry<T> option : this.options) {
            this.list.addOption(option);
        }
        if (this.initialValue != null) {
            this.list.selectByValue(this.initialValue);
        }
        this.addWidget(this.list);
    }

    public T getSelectedValue() {
        return this.list == null ? this.initialValue : this.list.getSelectedValue();
    }

    @Override
    protected float labelMaxWidth() {
        return this.width - LIST_WIDTH - 18.0f;
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        super.updatePos(x, y, width, height);
        this.list.updatePos(x + this.width - LIST_WIDTH - 6.0f, y + (this.height - ENTRY_HEIGHT) / 2.0f, LIST_WIDTH, ENTRY_HEIGHT);
    }
}
