package me.zyouime.holymoderation.gui.panel;

import lombok.Getter;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.Animated;
import me.zyouime.holymoderation.gui.widget.api.Elements;
import me.zyouime.holymoderation.gui.widget.api.Expandable;
import me.zyouime.holymoderation.gui.widget.api.ListWidget;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.utils.ScissorStack;
import org.joml.Matrix4fStack;

public class CategoryListWidget extends ListWidget<CategoryListWidget.CategoryEntry> implements Animated {

    private static final float SIDE_BLEED = 3.0f;
    private float contentHeight;
    private final BuiltRectangle scrollbar;
    private final BuiltRectangle scrollTrack;

    public CategoryListWidget(float x, float y, float width, float height, float spacing) {
        super(x, y, width, height, spacing, -1.0f);
        this.scrollbar = Builder.rectangle()
                .size(new SizeState(3.0f, 0.0f))
                .radius(new QuadRadiusState(1.5f))
                .color(new QuadColorState(GuiColors.BORDER))
                .build();
        this.scrollTrack = Builder.rectangle()
                .size(new SizeState(3.0f, 0.0f))
                .radius(new QuadRadiusState(1.5f))
                .color(new QuadColorState(GuiColors.withAlpha(GuiColors.DIVIDER, 90)))
                .build();
    }

    public void addCategory(AbstractCategory category) {
        category.init();
        this.addEntry(new CategoryEntry(category));
    }

    private float layout() {
        float offsetY = 0.0f;
        int index = 0;
        while (index < this.entries.size()) {
            CategoryEntry first = this.entries.get(index);
            float firstWidth = first.getCategory().getWidth();
            CategoryEntry second = index + 1 < this.entries.size() ? this.entries.get(index + 1) : null;
            float secondWidth = second == null ? Float.MAX_VALUE : second.getCategory().getWidth();
            boolean paired = second != null && firstWidth + this.spacing + secondWidth <= this.width;
            float top = (float) (this.y - this.getScrollAmount()) + offsetY;
            first.updatePos(this.x, top, firstWidth, 0.0f);
            if (paired) {
                second.updatePos(this.x + firstWidth + this.spacing, top, secondWidth, 0.0f);
                offsetY += Math.max(first.height, second.height) + this.spacing;
                index += 2;
            } else {
                offsetY += first.height + this.spacing;
                index++;
            }
        }
        return Math.max(0.0f, offsetY - this.spacing);
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.animScroll();
        this.contentHeight = this.layout();
        ScissorStack.push(this.x - SIDE_BLEED, this.y, this.x + this.width + SIDE_BLEED, this.y + this.height);
        try {
            for (CategoryEntry entry : Elements.expandedLast(this.entries)) {
                if (this.isEntryVisible(entry)) {
                    entry.render(matrices, mouseX, mouseY, delta);
                }
            }
        } finally {
            ScissorStack.pop();
        }
        this.renderScrollbar(matrices);
    }

    private void renderScrollbar(Matrix4fStack matrices) {
        float max = this.getScrollMax();
        if (max <= 0.5f) {
            return;
        }
        float ratio = this.height / this.contentHeight;
        float barHeight = Math.max(20.0f, this.height * ratio);
        float travel = this.height - barHeight;
        float barY = this.y + (float) (this.getScrollAmount() / max) * travel;
        float barX = this.x + this.width + 6.0f;
        this.scrollTrack.setSize(new SizeState(3.0f, this.height));
        this.scrollTrack.render(matrices, barX, this.y);
        this.scrollbar.setSize(new SizeState(3.0f, barHeight));
        this.scrollbar.render(matrices, barX, barY);
    }

    @Override
    public float getScrollMax() {
        return Math.max(0.0f, this.contentHeight - this.height);
    }

    @Override
    public void resetAnim() {
        this.resetScroll();
        for (CategoryEntry entry : this.entries) {
            entry.getCategory().resetAnim();
        }
    }

    public void collapseAll() {
        for (CategoryEntry entry : this.entries) {
            entry.getCategory().collapse();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (CategoryEntry entry : this.expandedFirst()) {
            if (entry.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        if (this.isOverAndActive(mouseX, mouseY)) {
            this.collapseAll();
            this.scrollBy(amount);
            return true;
        }
        return false;
    }

    @Getter
    public static class CategoryEntry extends ListEntry implements Expandable {

        private final AbstractCategory category;

        public CategoryEntry(AbstractCategory category) {
            this.category = category;
        }

        @Override
        protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
            this.category.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void updatePos(float x, float y, float width, float height) {
            this.category.updatePos(x, y, width, height);
            super.updatePos(x, y, this.category.getWidth(), this.category.getHeight());
        }

        @Override
        public boolean isExpanded() {
            return this.category.isExpanded();
        }

        @Override
        public void collapse() {
            this.category.collapse();
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.category.isMouseOver(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.category.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            return this.category.mouseScrolled(mouseX, mouseY, amount);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.category.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return this.category.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean keyPressed(int keyCode, int modifiers) {
            return this.category.keyPressed(keyCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int modifiers) {
            return this.category.keyReleased(keyCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return this.category.charTyped(chr, modifiers);
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            this.category.mouseMoved(mouseX, mouseY);
        }
    }
}
