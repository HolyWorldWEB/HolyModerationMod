package me.zyouime.holymoderation.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.gui.AbstractScreen;
import me.zyouime.holymoderation.gui.hud.Huds;
import me.zyouime.holymoderation.gui.notification.NotificationRenderer;
import me.zyouime.holymoderation.render.animation.Animation;
import me.zyouime.holymoderation.render.utils.ScissorStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.util.Window;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

    @Unique
    private Matrix4fStack matrices;

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"))
    private void renderHead(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        matrices = new Matrix4fStack(16);
        Animation.tick();
        ScissorStack.clear();
    }

    @Inject(method = "renderPreparedDraws", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Ljava/util/function/Supplier;Lnet/minecraft/client/gl/Framebuffer;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;II)V", ordinal = 0, shift = At.Shift.AFTER))
    private void renderBeforeBlur(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        Huds.render(matrices);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.AFTER))
    private void renderCustomLayer(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        MinecraftClient client = MinecraftProvider.client();
        float delta = client.getRenderTickCounter().getDynamicDeltaTicks();
        NotificationRenderer.render(matrices, delta);
        if (client.currentScreen instanceof AbstractScreen screen) {
            Window window = client.getWindow();
            screen.customRender(matrices, client.mouse.getScaledX(window), client.mouse.getScaledY(window), delta);
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("RETURN"))
    private void renderReturn(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        ScissorStack.clear();
    }
}
