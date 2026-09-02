package me.zyouime.holymoderation.gui.widget.impl.option;

import lombok.Getter;
import lombok.Setter;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;

import java.util.function.Consumer;

@Getter
@Setter
public abstract class AbstractOptionEntry<T> extends AbstractElement {

    protected T value;
    protected final Consumer<T> callback;
    protected boolean selected;

    public AbstractOptionEntry(Consumer<T> callback, T value) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.callback = callback;
        this.value = value;
    }

    public void init() {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return button == 0 && this.isOverAndActive(mouseX, mouseY);
    }

    public abstract AbstractOptionEntry<T> copy();

    public void apply() {
        if (this.callback != null) {
            this.callback.accept(this.value);
        }
    }
}
