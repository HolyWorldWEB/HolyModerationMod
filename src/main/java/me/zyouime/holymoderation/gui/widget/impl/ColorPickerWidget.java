package me.zyouime.holymoderation.gui.widget.impl;

import lombok.Getter;
import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.gui.widget.api.Animated;
import me.zyouime.holymoderation.gui.widget.api.Expandable;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltBorder;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;

import java.awt.Color;

public class ColorPickerWidget extends AbstractElement implements Animated, Expandable {

    private static final Color[] HUE_STOPS = {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED};
    private static final float CHECKER_SIZE = 4.0f;
    private final Setting<Color> setting;
    private final boolean hasAlpha;
    @Getter
    private final float buttonSize;
    private final float paletteWidth;
    private final float paletteHeight;
    private final float sliderWidth;
    private final float gap = 4.0f;
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha = 255;
    private float pickerX;
    private float pickerY;
    private boolean dragPalette;
    private boolean dragHue;
    private boolean dragAlpha;
    @Getter
    private boolean expanded;
    private final BuiltRectangle button;
    private final BuiltBorder buttonBorder;
    private final BuiltRectangle background;
    private final BuiltBorder backgroundBorder;
    private final BuiltRectangle palette;
    private final BuiltRectangle paletteShade;
    private final BuiltRectangle hueSegment;
    private final BuiltRectangle checker;
    private final BuiltRectangle alphaOverlay;
    private final BuiltRectangle pointerFill;
    private final BuiltBorder pointerBorder;
    private final float pointerSize;

    public ColorPickerWidget(float x, float y, float buttonSize, float paletteWidth, float paletteHeight, Setting<Color> setting, boolean hasAlpha) {
        super(x, y, buttonSize, buttonSize);
        this.setting = setting;
        this.hasAlpha = hasAlpha;
        this.buttonSize = buttonSize;
        this.paletteWidth = paletteWidth;
        this.paletteHeight = paletteHeight;
        this.sliderWidth = 10.0f;
        this.pointerSize = 7.0f;
        this.readSetting();
        this.button = Builder.rectangle()
                .size(new SizeState(buttonSize, buttonSize))
                .radius(new QuadRadiusState(3.0f))
                .color(QuadColorState.WHITE)
                .build();
        this.buttonBorder = Builder.border()
                .size(new SizeState(buttonSize, buttonSize))
                .radius(new QuadRadiusState(3.0f))
                .color(new QuadColorState(GuiColors.BORDER))
                .thickness(0.1f)
                .build();
        this.background = Builder.rectangle()
                .size(new SizeState(this.pickerWidth() + 8.0f, paletteHeight + 8.0f))
                .radius(new QuadRadiusState(5.0f))
                .color(new QuadColorState(GuiColors.CARD))
                .build();
        this.backgroundBorder = Builder.border()
                .size(this.background.getSize())
                .radius(this.background.getRadius())
                .color(new QuadColorState(GuiColors.BORDER))
                .thickness(0.05f)
                .build();
        this.palette = Builder.rectangle()
                .size(new SizeState(paletteWidth, paletteHeight))
                .radius(new QuadRadiusState(3.0f))
                .color(QuadColorState.WHITE)
                .build();
        this.paletteShade = Builder.rectangle()
                .size(this.palette.getSize())
                .radius(this.palette.getRadius())
                .color(QuadColorState.TRANSPARENT)
                .build();
        this.hueSegment = Builder.rectangle()
                .size(new SizeState(this.sliderWidth, paletteHeight / (HUE_STOPS.length - 1)))
                .radius(QuadRadiusState.NO_ROUND)
                .color(QuadColorState.WHITE)
                .build();
        this.checker = Builder.rectangle()
                .size(new SizeState(CHECKER_SIZE, CHECKER_SIZE))
                .radius(QuadRadiusState.NO_ROUND)
                .color(new QuadColorState(new Color(160, 160, 160)))
                .build();
        this.alphaOverlay = Builder.rectangle()
                .size(new SizeState(this.sliderWidth, paletteHeight))
                .radius(QuadRadiusState.NO_ROUND)
                .color(QuadColorState.TRANSPARENT)
                .build();
        this.pointerFill = Builder.rectangle()
                .size(new SizeState(this.pointerSize, this.pointerSize))
                .radius(new QuadRadiusState(this.pointerSize / 2.0f))
                .color(QuadColorState.WHITE)
                .build();
        this.pointerBorder = Builder.border()
                .size(new SizeState(this.pointerSize, this.pointerSize))
                .radius(new QuadRadiusState(this.pointerSize / 2.0f))
                .color(QuadColorState.WHITE)
                .thickness(0.22f)
                .build();
    }

    private float pickerWidth() {
        int sliders = this.hasAlpha ? 2 : 1;
        return this.paletteWidth + sliders * (this.sliderWidth + this.gap);
    }

    private void readSetting() {
        Color color = this.setting != null && this.setting.getValue() != null ? this.setting.getValue() : Color.WHITE;
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = color.getAlpha();
    }

    private Color currentColor() {
        int rgb = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, this.hasAlpha ? this.alpha : 255);
    }

    private void save() {
        if (this.setting != null) {
            this.setting.setValue(this.currentColor());
        }
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        super.updatePos(x, y, this.buttonSize, this.buttonSize);
        this.pickerX = x + this.buttonSize - this.pickerWidth();
        this.pickerY = y + this.buttonSize + 5.0f;
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.button.setColor(new QuadColorState(this.currentColor()));
        this.button.render(matrices, this.x, this.y);
        this.buttonBorder.render(matrices, this.x, this.y);

        if (this.expanded) {
            this.renderPicker(matrices);
        }
    }

    private void renderPicker(Matrix4fStack matrices) {
        this.background.render(matrices, this.pickerX - 4.0f, this.pickerY - 4.0f);
        this.backgroundBorder.render(matrices, this.pickerX - 4.0f, this.pickerY - 4.0f);
        this.renderPalette(matrices);
        float hueX = this.pickerX + this.paletteWidth + this.gap;
        this.renderHue(matrices, hueX);
        if (this.hasAlpha) {
            this.renderAlpha(matrices, hueX + this.sliderWidth + this.gap);
        }
    }

    private void renderPalette(Matrix4fStack matrices) {
        Color white = Color.getHSBColor(this.hue, 0.0f, 1.0f);
        Color pure = Color.getHSBColor(this.hue, 1.0f, 1.0f);
        Color transparent = new Color(0, 0, 0, 0);
        this.palette.setColor(new QuadColorState(white, white, pure, pure));
        this.palette.render(matrices, this.pickerX, this.pickerY);
        this.paletteShade.setColor(new QuadColorState(transparent, Color.BLACK, Color.BLACK, transparent));
        this.paletteShade.render(matrices, this.pickerX, this.pickerY);
        float pointerX = this.pickerX + this.saturation * this.paletteWidth - this.pointerSize / 2.0f;
        float pointerY = this.pickerY + (1.0f - this.brightness) * this.paletteHeight - this.pointerSize / 2.0f;
        this.renderPointer(matrices, pointerX, pointerY, Color.getHSBColor(this.hue, this.saturation, this.brightness));
    }

    private void renderHue(Matrix4fStack matrices, float x) {
        float segmentHeight = this.paletteHeight / (HUE_STOPS.length - 1);
        this.hueSegment.setSize(new SizeState(this.sliderWidth, segmentHeight + 0.5f));
        for (int i = 0; i < HUE_STOPS.length - 1; i++) {
            Color top = HUE_STOPS[i];
            Color bottom = HUE_STOPS[i + 1];
            this.hueSegment.setColor(new QuadColorState(top, bottom, bottom, top));
            this.hueSegment.render(matrices, x, this.pickerY + i * segmentHeight);
        }
        float pointerY = this.pickerY + this.hue * this.paletteHeight - this.pointerSize / 2.0f;
        this.renderPointer(matrices, x + this.sliderWidth / 2.0f - this.pointerSize / 2.0f, pointerY, Color.getHSBColor(this.hue, 1.0f, 1.0f));
    }

    private void renderAlpha(Matrix4fStack matrices, float x) {
        int rows = (int) Math.ceil(this.paletteHeight / CHECKER_SIZE);
        int columns = (int) Math.ceil(this.sliderWidth / CHECKER_SIZE);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if ((row + column) % 2 != 0) {
                    continue;
                }
                float cellWidth = Math.min(CHECKER_SIZE, this.sliderWidth - column * CHECKER_SIZE);
                float cellHeight = Math.min(CHECKER_SIZE, this.paletteHeight - row * CHECKER_SIZE);
                this.checker.setSize(new SizeState(cellWidth, cellHeight));
                this.checker.render(matrices, x + column * CHECKER_SIZE, this.pickerY + row * CHECKER_SIZE);
            }
        }
        Color color = Color.getHSBColor(this.hue, this.saturation, this.brightness);
        Color transparent = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        this.alphaOverlay.setColor(new QuadColorState(color, transparent, transparent, color));
        this.alphaOverlay.render(matrices, x, this.pickerY);
        float pointerY = this.pickerY + (1.0f - this.alpha / 255.0f) * this.paletteHeight - this.pointerSize / 2.0f;
        this.renderPointer(matrices, x + this.sliderWidth / 2.0f - this.pointerSize / 2.0f, pointerY, color);
    }

    private void renderPointer(Matrix4fStack matrices, float x, float y, Color color) {
        this.pointerFill.setColor(new QuadColorState(color));
        this.pointerFill.render(matrices, x, y);
        this.pointerBorder.render(matrices, x, y);
    }

    @Override
    public void resetAnim() {
        this.expanded = false;
    }

    @Override
    public void collapse() {
        this.expanded = false;
        this.dragAlpha = false;
        this.dragHue = false;
        this.dragPalette = false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }
        return this.expanded && this.insidePicker(mouseX, mouseY);
    }

    private boolean insidePicker(double mouseX, double mouseY) {
        return mouseX >= this.pickerX && mouseX <= this.pickerX + this.pickerWidth()
                && mouseY >= this.pickerY && mouseY <= this.pickerY + this.paletteHeight;
    }

    @Override
    public void resetSetting() {
        if (this.setting == null) {
            return;
        }
        this.setting.reset();
        this.readSetting();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active) {
            return false;
        }
        if (super.isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                this.resetSetting();
                ModSounds.playClick();
                return true;
            }
            this.expanded = !this.expanded;
            ModSounds.playClick(this.expanded ? 1.1f : 0.9f);
            return true;
        }
        if (this.expanded && this.insidePicker(mouseX, mouseY)) {
            float hueX = this.pickerX + this.paletteWidth + this.gap;
            float alphaX = hueX + this.sliderWidth + this.gap;
            if (mouseX <= this.pickerX + this.paletteWidth) {
                this.dragPalette = true;
                this.calcPalette(mouseX, mouseY);
            } else if (mouseX >= hueX && mouseX <= hueX + this.sliderWidth) {
                this.dragHue = true;
                this.calcHue(mouseY);
            } else if (this.hasAlpha && mouseX >= alphaX && mouseX <= alphaX + this.sliderWidth) {
                this.dragAlpha = true;
                this.calcAlpha(mouseY);
            }
            return true;
        }
        if (this.expanded) {
            this.collapse();
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragPalette || this.dragHue || this.dragAlpha) {
            this.dragPalette = false;
            this.dragHue = false;
            this.dragAlpha = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.expanded) {
            return false;
        }
        if (this.dragPalette) {
            this.calcPalette(mouseX, mouseY);
            return true;
        }
        if (this.dragHue) {
            this.calcHue(mouseY);
            return true;
        }
        if (this.dragAlpha) {
            this.calcAlpha(mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.expanded && !this.insidePicker(mouseX, mouseY)) {
            this.collapse();
        }
        return this.expanded && this.insidePicker(mouseX, mouseY);
    }

    private void calcPalette(double mouseX, double mouseY) {
        this.saturation = MathHelper.clamp((float) ((mouseX - this.pickerX) / this.paletteWidth), 0.0f, 1.0f);
        this.brightness = MathHelper.clamp((float) (1.0 - (mouseY - this.pickerY) / this.paletteHeight), 0.0f, 1.0f);
        this.save();
    }

    private void calcHue(double mouseY) {
        this.hue = MathHelper.clamp((float) ((mouseY - this.pickerY) / this.paletteHeight), 0.0f, 1.0f);
        this.save();
    }

    private void calcAlpha(double mouseY) {
        this.alpha = MathHelper.clamp((int) Math.round(255.0 * (1.0 - (mouseY - this.pickerY) / this.paletteHeight)), 0, 255);
        this.save();
    }
}
