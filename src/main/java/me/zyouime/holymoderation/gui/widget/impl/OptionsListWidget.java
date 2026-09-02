package me.zyouime.holymoderation.gui.widget.impl;

import lombok.Getter;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.Animated;
import me.zyouime.holymoderation.gui.widget.api.Expandable;
import me.zyouime.holymoderation.gui.widget.api.ListWidget;
import me.zyouime.holymoderation.gui.widget.impl.option.AbstractOptionEntry;
import me.zyouime.holymoderation.render.animation.Animation;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltBorder;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.utils.ScissorStack;
import org.joml.Matrix4fStack;

import java.util.Objects;

public class OptionsListWidget<T> extends ListWidget<OptionsListWidget.OptionsListWidgetEntry<T>> implements Animated, Expandable {

    private static final float GAP = 2.0f;
    private final float maxDropdownHeight;
    private boolean expanded;
    private float animHeight;
    @Getter
    private AbstractOptionEntry<T> header;
    @Getter
    private T selectedValue;
    private final BuiltRectangle background;
    private final BuiltBorder border;
    private final BuiltRectangle arrow;

    public OptionsListWidget(float x, float y, float width, float height, float maxDropdownHeight, float spacing) {
        super(x, y, width, height, spacing, height);
        this.maxDropdownHeight = maxDropdownHeight;
        this.background = Builder.rectangle()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(GuiColors.ELEMENT_BACK))
                .build();
        this.border = Builder.border()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(GuiColors.BORDER))
                .thickness(0.08f)
                .build();
        this.arrow = Builder.rectangle()
                .size(new SizeState(5.0f, 1.5f))
                .radius(new QuadRadiusState(0.75f))
                .color(new QuadColorState(GuiColors.TEXT_MUTED))
                .build();
    }

    public void addOption(AbstractOptionEntry<T> option) {
        option.init();
        this.addEntry(new OptionsListWidgetEntry<>(option));
        if (this.header == null) {
            this.select(option, false);
        }
    }

    public void selectByValue(T value) {
        for (OptionsListWidgetEntry<T> entry : this.entries) {
            if (Objects.equals(entry.getOption().getValue(), value)) {
                this.select(entry.getOption(), false);
                return;
            }
        }
    }

    public void select(AbstractOptionEntry<T> option, boolean fireCallback) {
        for (OptionsListWidgetEntry<T> entry : this.entries) {
            entry.getOption().setSelected(entry.getOption() == option);
        }
        this.header = option.copy();
        this.header.setSelected(true);
        this.selectedValue = option.getValue();
        if (fireCallback) {
            option.apply();
        }
    }

    private float dropdownTop() {
        return this.y + this.height + GAP;
    }

    private boolean insideDropdown(double mouseX, double mouseY) {
        float top = this.dropdownTop();
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= top && mouseY <= top + this.animHeight;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }
        return this.expanded && this.insideDropdown(mouseX, mouseY);
    }

    @Override
    public boolean isEntryVisible(OptionsListWidgetEntry<T> entry) {
        if (this.animHeight <= 0.5f) {
            return false;
        }
        float top = this.dropdownTop();
        return entry.y + entry.height >= top && entry.y <= top + this.animHeight;
    }

    @Override
    public float getScrollMax() {
        if (this.entries.isEmpty()) {
            return 0.0f;
        }
        float content = this.entries.size() * this.getEntryHeight() + this.spacing * Math.max(0, this.entries.size() - 1);
        return Math.max(0.0f, content - this.maxDropdownHeight);
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.animScroll();
        this.animHeight = (float) Animation.fast(this.animHeight, this.expanded ? this.visibleDropdownHeight() : 0.0f, 20.0);
        this.background.setSize(new SizeState(this.width, this.height));
        this.border.setSize(this.background.getSize());
        this.background.render(matrices, this.x, this.y);
        this.border.render(matrices, this.x, this.y);
        if (this.header != null) {
            this.header.updatePos(this.x, this.y, this.width - 12.0f, this.height);
            this.header.render(matrices, mouseX, mouseY, delta);
        }
        this.renderArrow(matrices);
        if (this.animHeight > 0.5f) {
            this.renderDropdown(matrices, mouseX, mouseY, delta);
        }
    }

    private float visibleDropdownHeight() {
        float content = this.entries.size() * this.getEntryHeight() + this.spacing * Math.max(0, this.entries.size() - 1);
        return Math.min(this.maxDropdownHeight, content) + 2.0f;
    }

    private void renderArrow(Matrix4fStack matrices) {
        float centerX = this.x + this.width - 8.0f;
        float centerY = this.y + this.height / 2.0f;
        float direction = this.expanded ? -1.0f : 1.0f;
        matrices.pushMatrix();
        matrices.translate(centerX - 1.5f, centerY, 0.0f);
        matrices.rotateZ((float) Math.toRadians(45.0 * direction));
        this.arrow.render(matrices, -2.5f, -0.75f);
        matrices.popMatrix();
        matrices.pushMatrix();
        matrices.translate(centerX + 1.5f, centerY, 0.0f);
        matrices.rotateZ((float) Math.toRadians(-45.0 * direction));
        this.arrow.render(matrices, -2.5f, -0.75f);
        matrices.popMatrix();
    }

    private void renderDropdown(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        float top = this.dropdownTop();
        {
            this.background.setSize(new SizeState(this.width, this.animHeight));
            this.border.setSize(this.background.getSize());
            this.background.render(matrices, this.x, top);
            this.border.render(matrices, this.x, top);
            ScissorStack.pushOverride(this.x, top + 1.0f, this.x + this.width, top + this.animHeight - 1.0f);
            try {
                float offset = 1.0f;
                for (OptionsListWidgetEntry<T> entry : this.entries) {
                    entry.updatePos(this.x, (float) (top - this.getScrollAmount()) + offset, this.width, this.getEntryHeight());
                    if (this.isEntryVisible(entry)) {
                        entry.render(matrices, mouseX, mouseY, delta);
                    }
                    offset += entry.height + this.spacing;
                }
            } finally {
                ScissorStack.pop();
            }
        }
    }

    @Override
    public boolean isExpanded() {
        return this.expanded;
    }

    @Override
    public void collapse() {
        if (this.expanded) {
            this.expanded = false;
            this.resetScroll();
        }
    }

    @Override
    public void resetAnim() {
        this.animHeight = 0.0f;
        this.expanded = false;
        this.resetScroll();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active) {
            return false;
        }
        if (this.expanded && this.insideDropdown(mouseX, mouseY)) {
            if (button == 0) {
                for (OptionsListWidgetEntry<T> entry : this.entries) {
                    if (this.isEntryVisible(entry) && entry.isMouseOver(mouseX, mouseY)) {
                        this.select(entry.getOption(), true);
                        this.collapse();
                        ModSounds.playClick();
                        return true;
                    }
                }
            }
            return true;
        }
        if (super.isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                this.resetSetting();
                return true;
            }
            if (this.expanded) {
                this.collapse();
            } else {
                this.expanded = true;
            }
            ModSounds.playClick(this.expanded ? 1.1f : 0.9f);
            return true;
        }
        this.collapse();
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.expanded) {
            return false;
        }
        if (this.insideDropdown(mouseX, mouseY)) {
            this.scrollBy(amount);
            return true;
        }
        this.collapse();
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public static class OptionsListWidgetEntry<T> extends ListEntry {

        @Getter
        private final AbstractOptionEntry<T> option;

        public OptionsListWidgetEntry(AbstractOptionEntry<T> option) {
            this.option = option;
        }

        @Override
        protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
            this.option.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void updatePos(float x, float y, float width, float height) {
            super.updatePos(x, y, width, height);
            this.option.updatePos(x, y, width, height);
        }
    }
}
