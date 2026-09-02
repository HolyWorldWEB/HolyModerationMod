package me.zyouime.holymoderation.gui.widget.api;

import lombok.Getter;
import me.zyouime.holymoderation.render.animation.Animation;
import me.zyouime.holymoderation.render.utils.ScissorStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ListWidget<E extends ListWidget.ListEntry> extends AbstractElement {

    protected static final float SCROLL_STEP = 22.0f;
    @Getter
    private double scrollAmount;
    @Getter
    private double targetScrollAmount;
    @Getter
    private final float entryHeight;
    protected final float spacing;
    @Getter
    protected final List<E> entries = new ArrayList<>();

    public ListWidget(float x, float y, float width, float height, float spacing, float entryHeight) {
        super(x, y, width, height);
        this.spacing = spacing;
        this.entryHeight = entryHeight;
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.animScroll();
        this.renderList(matrices, mouseX, mouseY, delta);
    }

    public void addEntry(E entry) {
        this.entries.add(entry);
    }

    public void renderList(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        float offset = 0.0f;
        for (E entry : this.entries) {
            entry.updatePos(this.x, (float) (this.y - this.scrollAmount) + offset, this.width, this.entryHeight);
            offset += entry.height + this.spacing;
        }
        ScissorStack.push(this.x, this.y, this.x + this.width, this.y + this.height);
        try {
            for (E entry : Elements.expandedLast(this.entries)) {
                if (this.isEntryVisible(entry)) {
                    entry.render(matrices, mouseX, mouseY, delta);
                }
            }
        } finally {
            ScissorStack.pop();
        }
    }

    public boolean isEntryVisible(E entry) {
        return entry.y + entry.height >= this.y && entry.y <= this.y + this.height;
    }

    public void clear() {
        this.entries.clear();
    }

    public void animScroll() {
        this.targetScrollAmount = MathHelper.clamp(this.targetScrollAmount, 0.0, this.getScrollMax());
        this.scrollAmount = Animation.fast(this.scrollAmount, this.targetScrollAmount, 18.0);
    }

    public void resetScroll() {
        this.scrollAmount = 0.0;
        this.targetScrollAmount = 0.0;
    }

    public int getEntriesSize() {
        return this.entries.size();
    }

    public float getScrollMax() {
        if (this.entries.isEmpty()) {
            return 0.0f;
        }
        float totalHeight = 0.0f;
        for (E entry : this.entries) {
            totalHeight += entry.height;
        }
        totalHeight += this.spacing * Math.max(0, this.entries.size() - 1);
        return Math.max(0.0f, totalHeight - this.height);
    }

    public void scrollBy(double amount) {
        if (amount == 0.0) {
            return;
        }
        this.scroll(this.targetScrollAmount - Math.signum(amount) * SCROLL_STEP);
    }

    public void scroll(double amount) {
        this.targetScrollAmount = MathHelper.clamp(amount, 0.0, this.getScrollMax());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (E entry : this.entries) {
            if (this.isEntryVisible(entry) && entry.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        if (this.isOverAndActive(mouseX, mouseY)) {
            this.scrollBy(amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active) {
            return false;
        }
        for (E entry : this.expandedFirst()) {
            if (this.isEntryVisible(entry) && entry.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (E entry : this.entries) {
            if (entry.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (E entry : this.entries) {
            entry.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int modifiers) {
        for (E entry : this.entries) {
            if (entry.keyPressed(keyCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int modifiers) {
        for (E entry : this.entries) {
            if (entry.keyReleased(keyCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (E entry : this.entries) {
            if (entry.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (E entry : this.entries) {
            if (entry.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    protected List<E> expandedFirst() {
        return Elements.expandedFirst(this.entries);
    }

    public static abstract class ListEntry extends AbstractElement {

        public ListEntry() {
            super(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
}
