package me.zyouime.holymoderation.gui.panel.setting;

import lombok.Getter;
import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.gui.widget.api.Animated;
import me.zyouime.holymoderation.gui.widget.api.Elements;
import me.zyouime.holymoderation.gui.widget.api.Expandable;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.resources.Fonts;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PanelSetting<T> extends AbstractElement implements Animated, Expandable {

    public static final float DEFAULT_WIDTH = 167.0f;
    public static final float DEFAULT_HEIGHT = 20.0f;
    public static final float WIDE_WIDTH = 340.0f;
    private final Setting<T> setting;
    private final String settingName;
    private final List<AbstractElement> widgets = new ArrayList<>();
    private BuiltText label;
    private BuiltRectangle hover;

    public PanelSetting(float width, float height, Setting<T> setting, String settingName) {
        super(0.0f, 0.0f, width, height);
        this.setting = setting;
        this.settingName = settingName;
    }

    public void init() {
        this.widgets.clear();
        this.hover = Builder.rectangle()
                .size(SizeState.NONE)
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(GuiColors.ROW_HOVER))
                .build();
        this.label = Builder.text()
                .text(this.settingName)
                .size(8.0f)
                .color(GuiColors.TEXT)
                .thickness(0.05f)
                .smoothness(0.5f)
                .font(Fonts.UI.get())
                .build();
    }

    protected <W extends AbstractElement> W addWidget(W widget) {
        this.widgets.add(widget);
        return widget;
    }

    protected float labelMaxWidth() {
        return this.width * 0.55f;
    }

    protected void renderLabel(Matrix4fStack matrices) {
        if (this.label == null) {
            return;
        }
        this.label.setMaxWidth(this.labelMaxWidth());
        this.label.render(matrices, this.x + 10.0f, this.y + (this.height - this.label.getLineHeight()) / 2.0f);
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        if (this.hover != null && this.isOverAndActive(mouseX, mouseY)) {
            this.hover.setSize(new SizeState(this.width - 8.0f, this.height - 2.0f));
            this.hover.render(matrices, this.x + 4.0f, this.y + 1.0f);
        }
        this.renderLabel(matrices);
        for (AbstractElement widget : Elements.expandedLast(this.widgets)) {
            widget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean isExpanded() {
        for (AbstractElement widget : this.widgets) {
            if (widget instanceof Expandable expandable && expandable.isExpanded()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void collapse() {
        Elements.collapseAll(this.widgets);
    }

    @Override
    public void resetAnim() {
        for (AbstractElement widget : this.widgets) {
            if (widget instanceof Animated animated) {
                animated.resetAnim();
            }
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (AbstractElement widget : this.widgets) {
            if (widget.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int modifiers) {
        for (AbstractElement widget : this.widgets) {
            if (widget.keyPressed(keyCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int modifiers) {
        for (AbstractElement widget : this.widgets) {
            if (widget.keyReleased(keyCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (AbstractElement widget : Elements.expandedFirst(this.widgets)) {
            if (widget.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (AbstractElement widget : this.widgets) {
            if (widget.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (AbstractElement widget : this.widgets) {
            if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractElement widget : Elements.expandedFirst(this.widgets)) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        if (button == 1 && this.active && this.isMouseOver(mouseX, mouseY)) {
            this.resetSetting();
            ModSounds.playClick();
            return true;
        }
        return false;
    }

    @Override
    public void resetSetting() {
        for (AbstractElement widget : this.widgets) {
            widget.resetSetting();
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (AbstractElement widget : this.widgets) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }
}
