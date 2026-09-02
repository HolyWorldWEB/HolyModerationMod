package me.zyouime.holymoderation.gui.widget.impl;

import lombok.Setter;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltBorder;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.resources.Fonts;
import org.joml.Matrix4fStack;

import java.awt.Color;

public class ButtonWidget extends AbstractElement {

    @Setter
    private Runnable action;
    private boolean crossIcon;
    private BuiltRectangle crossBar;
    private final BuiltRectangle background;
    private final BuiltBorder border;
    private final BuiltText label;
    private final Color baseColor;

    public ButtonWidget(float x, float y, float width, float height, String text, Color accent, Runnable action) {
        super(x, y, width, height);
        this.action = action;
        this.baseColor = accent;
        this.background = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(GuiColors.withAlpha(accent, 60)))
                .build();
        this.border = Builder.border()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(GuiColors.withAlpha(accent, 190)))
                .thickness(0.08f)
                .build();
        this.label = Builder.text()
                .text(text)
                .size(Math.max(6.0f, height / 2.4f))
                .color(accent)
                .thickness(0.06f)
                .font(Fonts.UI.get())
                .build();
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public static ButtonWidget cross(float x, float y, float width, float height, Color accent, Runnable action) {
        ButtonWidget button = new ButtonWidget(x, y, width, height, "", accent, action);
        button.crossIcon = true;
        float thickness = Math.max(1.0f, height * 0.11f);
        button.crossBar = Builder.rectangle()
                .size(new SizeState(height * 0.42f, thickness))
                .radius(new QuadRadiusState(thickness / 2.0f))
                .color(new QuadColorState(accent))
                .build();
        return button;
    }

    private void renderCross(Matrix4fStack matrices) {
        float centerX = this.x + this.width / 2.0f;
        float centerY = this.y + this.height / 2.0f;
        float barWidth = this.crossBar.getSize().width();
        float barHeight = this.crossBar.getSize().height();
        for (int direction = -1; direction <= 1; direction += 2) {
            matrices.pushMatrix();
            matrices.translate(centerX, centerY, 0.0f);
            matrices.rotateZ((float) Math.toRadians(45.0 * direction));
            this.crossBar.render(matrices, -barWidth / 2.0f, -barHeight / 2.0f);
            matrices.popMatrix();
        }
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        boolean hovered = this.isOverAndActive(mouseX, mouseY);
        this.background.setSize(new SizeState(this.width, this.height));
        this.border.setSize(new SizeState(this.width, this.height));
        this.background.setColor(new QuadColorState(GuiColors.withAlpha(this.baseColor, hovered ? 105 : 55)));
        this.background.render(matrices, this.x, this.y);
        this.border.render(matrices, this.x, this.y);
        if (this.crossIcon) {
            this.renderCross(matrices);
            return;
        }
        this.label.renderCenteredX(matrices, this.x + this.width / 2.0f, this.y + (this.height - this.label.getLineHeight()) / 2.0f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isOverAndActive(mouseX, mouseY)) {
            if (this.action != null) {
                this.action.run();
            }
            ModSounds.playClick();
            return true;
        }
        return false;
    }
}
