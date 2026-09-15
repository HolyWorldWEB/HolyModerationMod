package me.zyouime.holymoderation.gui.widget.api;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

public interface Element {

    default boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    default void mouseMoved(double mouseX, double mouseY) {
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    default boolean keyPressed(KeyInput input) {
        return false;
    }

    default boolean keyReleased(KeyInput input) {
        return false;
    }

    default boolean charTyped(CharInput input) {
        return false;
    }
}
