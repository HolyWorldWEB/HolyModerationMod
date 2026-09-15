package me.zyouime.holymoderation.gui;

import lombok.Setter;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.gui.widget.api.Elements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScreen extends Screen {

    @Setter
    protected Screen parent;
    protected final List<AbstractElement> widgets = new ArrayList<>();

    public AbstractScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
    }

    public abstract void customRender(Matrix4fStack matrices, double mouseX, double mouseY, float delta);

    protected <W extends AbstractElement> W addWidget(W widget) {
        this.widgets.add(widget);
        return widget;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    protected double mouseX() {
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        return client.mouse.getScaledX(window);
    }

    protected double mouseY() {
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();
        return client.mouse.getScaledY(window);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = this.mouseX();
        double mouseY = this.mouseY();
        for (AbstractElement widget : Elements.expandedFirst(this.widgets)) {
            if (widget.mouseClicked(mouseX, mouseY, click.button())) {
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        double mouseX = this.mouseX();
        double mouseY = this.mouseY();
        for (AbstractElement widget : this.widgets) {
            if (widget.mouseReleased(mouseX, mouseY, click.button())) {
                return true;
            }
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        double mouseX = this.mouseX();
        double mouseY = this.mouseY();
        for (AbstractElement widget : this.widgets) {
            if (widget.mouseDragged(mouseX, mouseY, click.button(), offsetX, offsetY)) {
                return true;
            }
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (AbstractElement widget : Elements.expandedFirst(this.widgets)) {
            if (widget.mouseScrolled(mouseX, mouseY, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (AbstractElement widget : this.widgets) {
            widget.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        for (AbstractElement widget : this.widgets) {
            if (widget.keyPressed(input)) {
                return true;
            }
        }
        if (input.isEscape() && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        for (AbstractElement widget : this.widgets) {
            if (widget.keyReleased(input)) {
                return true;
            }
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        String typed = input.asString();
        boolean handled = false;
        for (int i = 0; i < typed.length(); i++) {
            for (AbstractElement widget : this.widgets) {
                if (widget.charTyped(new CharInput(typed.charAt(i), 0))) {
                    handled = true;
                    break;
                }
            }
        }
        return handled || super.charTyped(input);
    }
}
