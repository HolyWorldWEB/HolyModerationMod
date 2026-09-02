package me.zyouime.holymoderation.mixin;

import me.zyouime.holymoderation.gui.hud.Huds;
import me.zyouime.holymoderation.gui.hud.SpyHud;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (Huds.mouseClicked(click.x(), click.y(), click.button())) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (Huds.mouseReleased(click.x(), click.y(), click.button())) {
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (Huds.mouseDragged(click.x(), click.y(), click.button())) {
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }
}
