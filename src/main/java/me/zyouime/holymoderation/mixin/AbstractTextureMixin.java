package me.zyouime.holymoderation.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import me.zyouime.holymoderation.render.utils.AbstractTextureHelper;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractTexture.class)
public abstract class AbstractTextureMixin implements AbstractTextureHelper {

    @Shadow
    protected GpuSampler sampler;

    @Override
    public void setMsdfFilter() {
        this.sampler = RenderSystem.getSamplerCache().get(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.LINEAR, FilterMode.LINEAR, false);
    }
}