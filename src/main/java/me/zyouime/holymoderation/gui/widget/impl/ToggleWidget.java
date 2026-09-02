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
import org.joml.Matrix4fStack;

public class ToggleWidget extends AbstractElement {

    private static final float KNOB_INSET = 2.0f;
    private final Setting<Boolean> setting;
    @Getter
    private boolean value;
    private double progress;
    private final BuiltRectangle track;
    private final BuiltRectangle knob;

    public ToggleWidget(float x, float y, float width, float height, Setting<Boolean> setting) {
        super(x, y, width, height);
        this.setting = setting;
        this.value = setting != null && Boolean.TRUE.equals(setting.getValue());
        this.progress = this.value ? 1.0 : 0.0;
        this.track = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(height / 2.0f))
                .color(new QuadColorState(GuiColors.TRACK))
                .build();
        float knobSize = height - KNOB_INSET * 2.0f;
        this.knob = Builder.rectangle()
                .size(new SizeState(knobSize, knobSize))
                .color(new QuadColorState(GuiColors.KNOB))
                .build();
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.progress = Animation.fast(this.progress, this.value ? 1.0 : 0.0, 24.0);
        float knobSize = this.height - KNOB_INSET * 2.0f;
        this.track.setSize(new SizeState(this.width, this.height));
        this.track.setRadius(new QuadRadiusState(this.height / 2.5f));
        this.track.setColor(new QuadColorState(GuiColors.mix(GuiColors.TRACK, GuiColors.ACCENT, (float) this.progress)));
        this.track.render(matrices, this.x, this.y);
        this.knob.setSize(new SizeState(knobSize, knobSize));
        this.knob.setRadius(new QuadRadiusState(knobSize / 2.5f));
        float travel = this.width - knobSize - KNOB_INSET * 2.0f;
        this.knob.render(matrices, this.x + KNOB_INSET + travel * (float) this.progress, this.y + KNOB_INSET);
    }

    public void setValue(boolean value) {
        this.value = value;
        if (this.setting != null) {
            this.setting.setValue(value);
        }
    }

    @Override
    public void resetSetting() {
        if (this.setting == null) {
            return;
        }
        this.setting.reset();
        this.value = Boolean.TRUE.equals(this.setting.getValue());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 && this.isOverAndActive(mouseX, mouseY)) {
            this.setValue(!this.value);
            ModSounds.playSound(this.value ? ModSounds.ON : ModSounds.OFF);
            return true;
        }
        return false;
    }
}
