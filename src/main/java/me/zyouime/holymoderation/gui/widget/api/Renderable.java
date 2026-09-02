package me.zyouime.holymoderation.gui.widget.api;

import org.joml.Matrix4fStack;

public interface Renderable {

    void render(Matrix4fStack matrices, double mouseX, double mouseY, float delta);
}
