package me.zyouime.holymoderation.gui.widget.api;

import lombok.Getter;
import lombok.Setter;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.joml.Matrix4fStack;

@Getter
@Setter
public abstract class AbstractElement implements Element, Renderable {

    public float x;
    public float y;
    public float width;
    public float height;
    public boolean active = true;

    public AbstractElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public boolean isOverAndActive(double mouseX, double mouseY) {
        return this.active && this.isMouseOver(mouseX, mouseY);
    }

    public void updatePos(float x, float y) {
        this.updatePos(x, y, this.width, this.height);
    }

    public void updatePos(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public final void render(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.renderElement(matrices, mouseX, mouseY, delta);
    }

    protected abstract void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta);

    public void resetSetting() {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && this.isOverAndActive(mouseX, mouseY)) {
            this.resetSetting();
            ModSounds.playClick();
            return true;
        }
        return false;
    }

}
