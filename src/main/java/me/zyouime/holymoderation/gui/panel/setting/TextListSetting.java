package me.zyouime.holymoderation.gui.panel.setting;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.impl.ButtonWidget;
import me.zyouime.holymoderation.gui.widget.impl.SimpleTextFieldWidget;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.resources.Fonts;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

public class TextListSetting extends PanelSetting<List<String>> {

    private static final float ROW_HEIGHT = 15.0f;
    private static final float ROW_SPACING = 3.0f;
    private static final float DELETE_WIDTH = 16.0f;
    private static final float HEADER_HEIGHT = 16.0f;
    private static final float FOOTER_HEIGHT = 18.0f;
    private static final int MAX_ROWS = 32;
    private final int maxLength;
    private final List<Row> rows = new ArrayList<>();
    private ButtonWidget addButton;
    private BuiltText hint;

    public TextListSetting(Setting<List<String>> setting, String settingName, int maxLength) {
        this(WIDE_WIDTH, setting, settingName, maxLength);
    }

    public TextListSetting(float width, Setting<List<String>> setting, String settingName, int maxLength) {
        super(width, HEADER_HEIGHT + FOOTER_HEIGHT, setting, settingName);
        this.maxLength = maxLength;
    }

    @Override
    public void init() {
        super.init();
        this.hint = Builder.text()
                .text("крестик удаляет строку")
                .size(6.0f)
                .color(GuiColors.TEXT_MUTED)
                .thickness(0.05f)
                .font(Fonts.UI.get())
                .build();
        this.addButton = new ButtonWidget(0.0f, 0.0f, 60.0f, 13.0f, "+ добавить", GuiColors.ACCENT, this::addRow);
        this.rebuild(this.currentValues());
    }

    private List<String> currentValues() {
        List<String> value = this.getSetting() == null ? null : this.getSetting().getValue();
        return value == null ? new ArrayList<>() : new ArrayList<>(value);
    }

    private void rebuild(List<String> values) {
        this.rows.clear();
        for (String value : values) {
            this.rows.add(this.createRow(value));
        }
        this.syncWidgets();
    }

    private void syncWidgets() {
        List<me.zyouime.holymoderation.gui.widget.api.AbstractElement> widgets = this.getWidgets();
        widgets.clear();
        for (Row row : this.rows) {
            widgets.add(row.field);
            widgets.add(row.delete);
        }
        widgets.add(this.addButton);
    }

    private Row createRow(String value) {
        SimpleTextFieldWidget field = new SimpleTextFieldWidget(0.0f, 0.0f, this.width - DELETE_WIDTH - 16.0f, ROW_HEIGHT, null);
        field.setMaxLength(this.maxLength);
        field.setPlaceholder("текст сообщения");
        field.setText(value);
        field.setCallback(text -> this.save());
        ButtonWidget delete = ButtonWidget.cross(0.0f, 0.0f, DELETE_WIDTH, ROW_HEIGHT, GuiColors.DANGER, null);
        Row row = new Row(field, delete);
        delete.setAction(() -> this.removeRow(row));
        return row;
    }

    private void addRow() {
        if (this.rows.size() >= MAX_ROWS) {
            return;
        }
        this.rows.add(this.createRow(""));
        this.syncWidgets();
        this.save();
    }

    private void removeRow(Row row) {
        if (!this.rows.remove(row)) {
            return;
        }
        this.syncWidgets();
        this.save();
    }

    private void save() {
        if (this.getSetting() == null) {
            return;
        }
        List<String> values = new ArrayList<>(this.rows.size());
        for (Row row : this.rows) {
            values.add(row.field.getText());
        }
        this.getSetting().setValue(values);
    }

    @Override
    public void resetSetting() {
        if (this.getSetting() == null) {
            return;
        }
        this.getSetting().reset();
        this.rebuild(this.currentValues());
    }

    @Override
    protected float labelMaxWidth() {
        return this.width - 12.0f;
    }

    @Override
    protected void renderLabel(Matrix4fStack matrices) {
        if (this.getLabel() == null) {
            return;
        }
        float hintWidth = this.hint == null ? 0.0f : this.hint.getTextWidth();
        this.getLabel().setMaxWidth(this.width - hintWidth - 18.0f);
        this.getLabel().render(matrices, this.x + 6.0f, this.y + 2.0f);
        if (this.hint != null) {
            this.hint.render(matrices, this.x + this.width - hintWidth - 12f, this.y + 3.5f);
        }
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = HEADER_HEIGHT + this.rows.size() * (ROW_HEIGHT + ROW_SPACING) + FOOTER_HEIGHT;
        float rowY = y + HEADER_HEIGHT;
        float fieldWidth = this.width - DELETE_WIDTH - 16.0f;
        for (Row row : this.rows) {
            row.field.updatePos(x + 6.0f, rowY, fieldWidth, ROW_HEIGHT);
            row.delete.updatePos(x + 6.0f + fieldWidth + 4.0f, rowY, DELETE_WIDTH, ROW_HEIGHT);
            rowY += ROW_HEIGHT + ROW_SPACING;
        }
        this.addButton.updatePos(x + 6.0f, rowY + 1.0f, 60.0f, 13.0f);
    }

    private record Row(SimpleTextFieldWidget field, ButtonWidget delete) {

    }
}
