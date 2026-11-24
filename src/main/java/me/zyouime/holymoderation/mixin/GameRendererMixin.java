package me.zyouime.holymoderation.mixin;

import me.zyouime.holymoderation.render.animation.Animation;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/render/GameRenderer;guiRenderer:Lnet/minecraft/client/gui/render/GuiRenderer;", ordinal = 0))
    private void holymoderation$beginGuiFrame(RenderTickCounter counter, boolean tick, CallbackInfo ci) {
        Animation.beginFrame(System.nanoTime());
    }
}
