package me.zyouime.holymoderation.gui.widget.api;

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

    default boolean keyPressed(int keyCode, int modifiers) {
        return false;
    }

    default boolean keyReleased(int keyCode, int modifiers) {
        return false;
    }

    default boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
