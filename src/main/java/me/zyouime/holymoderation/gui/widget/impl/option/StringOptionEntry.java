package me.zyouime.holymoderation.gui.widget.impl.option;

import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.resources.Fonts;
import org.joml.Matrix4fStack;

import java.util.function.Consumer;

public class StringOptionEntry extends AbstractOptionEntry<String> {

    private BuiltText text;
    private BuiltRectangle highlight;

    public StringOptionEntry(Consumer<String> callback, String value) {
        super(callback, value);
    }

    @Override
    public void init() {
        this.text = Builder.text()
                .size(7.5f)
                .font(Fonts.UI.get())
                .thickness(0.05f)
                .color(GuiColors.TEXT)
                .text(this.value)
                .build();
        this.highlight = Builder.rectangle()
                .size(SizeState.NONE)
                .radius(new QuadRadiusState(3.0f))
                .color(new QuadColorState(GuiColors.withAlpha(GuiColors.ACCENT, 55)))
                .build();
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        if (this.text == null) {
            this.init();
        }
        boolean hovered = this.isOverAndActive(mouseX, mouseY);
        if (this.selected || hovered) {
            this.highlight.setSize(new SizeState(this.width - 4.0f, this.height - 1.0f));
            this.highlight.render(matrices, this.x + 2.0f, this.y + 0.5f);
        }
        this.text.setMaxWidth(Math.max(0.0f, this.width - 10.0f));
        this.text.setColor(this.selected ? GuiColors.TEXT : GuiColors.TEXT_MUTED);
        this.text.render(matrices, this.x + 5.0f, this.y + (this.height - this.text.getLineHeight()) / 2.0f);
    }

    @Override
    public StringOptionEntry copy() {
        StringOptionEntry entry = new StringOptionEntry(this.callback, this.value);
        entry.updatePos(this.x, this.y, this.width, this.height);
        entry.init();
        return entry;
    }
}
