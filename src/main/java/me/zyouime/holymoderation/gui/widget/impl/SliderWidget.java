package me.zyouime.holymoderation.gui.widget.impl;

import lombok.Getter;
import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.render.animation.Animation;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.resources.Fonts;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;

import java.awt.*;
import java.util.function.Function;

public class SliderWidget<T extends Number> extends AbstractElement {

    private static final float KNOB_SIZE = 9.0f;
    private static final float HIT_PADDING = 5.0f;
    private final Setting<T> setting;
    private final Function<Float, T> mapper;
    private final float min;
    private final float max;
    @Getter
    private float value;
    @Getter
    private boolean dragging;
    private double knobScale = 1.0;
    private final BuiltRectangle track;
    private final BuiltRectangle fill;
    private final BuiltRectangle knob;
    private final BuiltText valueText;

    public static SliderWidget<Integer> integer(float x, float y, float width, float height, int min, int max, Setting<Integer> setting) {
        return new SliderWidget<>(x, y, width, height, min, max, setting, Math::round);
    }

    public SliderWidget(float x, float y, float width, float height, float min, float max, Setting<T> setting, Function<Float, T> mapper) {
        super(x, y, width, height);
        this.min = min;
        this.max = Math.max(max, min + 0.0001f);
        this.setting = setting;
        this.mapper = mapper;
        this.value = setting != null && setting.getValue() != null ? setting.getValue().floatValue() : min;
        this.track = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(height / 2.0f))
                .color(new QuadColorState(GuiColors.ELEMENT_BACK))
                .build();
        this.fill = Builder.rectangle()
                .size(new SizeState(0.0f, height))
                .radius(new QuadRadiusState(height / 2.0f))
                .color(new QuadColorState(GuiColors.ACCENT))
                .build();
        this.knob = Builder.rectangle()
                .size(new SizeState(KNOB_SIZE, KNOB_SIZE))
                .color(new QuadColorState(GuiColors.KNOB))
                .build();
        this.valueText = Builder.text()
                .size(8.0f)
                .color(GuiColors.TEXT_MUTED)
                .font(Fonts.UI.get())
                .thickness(0.05f)
                .text(this.formatValue())
                .build();
    }

    private String formatValue() {
        return this.mapper == null ? String.valueOf(this.value) : String.valueOf(this.mapper.apply(this.value));
    }

    private float progress() {
        return MathHelper.clamp((this.value - this.min) / (this.max - this.min), 0.0f, 1.0f);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x - HIT_PADDING && mouseX <= this.x + this.width + HIT_PADDING && mouseY >= this.y - HIT_PADDING && mouseY <= this.y + this.height + HIT_PADDING;
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        boolean active = this.dragging || this.isOverAndActive(mouseX, mouseY);
        this.knobScale = Animation.fast(this.knobScale, active ? 1.25 : 1.0, 20.0);

        float filled = this.width * this.progress();
        this.track.setSize(new SizeState(this.width, this.height));
        this.track.render(matrices, this.x, this.y);
        if (filled > 0.5f) {
            this.fill.setSize(new SizeState(filled, this.height));
            this.fill.render(matrices, this.x, this.y);
        }

        float size = KNOB_SIZE * (float) this.knobScale;
        this.knob.setSize(new SizeState(size, size));
        this.knob.setRadius(new QuadRadiusState(size / 2.5f));
        this.knob.render(matrices, this.x + MathHelper.clamp(filled - size / 2.0f, 0.0f, this.width - size), this.y + (this.height - size) / 2.0f);
        this.valueText.setText(this.formatValue());
        this.valueText.setColor(active ? GuiColors.TEXT : GuiColors.TEXT_MUTED);
        this.valueText.render(matrices, this.x + this.width - this.valueText.getTextWidth(), this.y - this.valueText.getLineHeight() - 10.0f);
    }

    private void setValue(double raw) {
        float clamped = (float) MathHelper.clamp(raw, this.min, this.max);
        if (clamped == this.value) {
            return;
        }
        this.value = clamped;
        if (this.setting != null && this.mapper != null) {
            this.setting.setValue(this.mapper.apply(this.value));
        }
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue(((mouseX - this.x) / this.width) * (this.max - this.min) + this.min);
    }

    @Override
    public void resetSetting() {
        if (this.setting == null) {
            return;
        }
        this.setting.reset();
        if (this.setting.getValue() != null) {
            this.value = this.setting.getValue().floatValue();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 && this.isOverAndActive(mouseX, mouseY)) {
            this.dragging = true;
            this.setValueFromMouse(mouseX);
            ModSounds.playClick(1.1f);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragging) {
            this.dragging = false;
            ModSounds.playClick(0.9f);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.dragging) {
            this.setValueFromMouse(mouseX);
            return true;
        }
        return false;
    }
}
