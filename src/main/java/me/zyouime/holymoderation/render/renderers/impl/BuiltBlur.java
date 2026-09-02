package me.zyouime.holymoderation.render.renderers.impl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import lombok.Data;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.IRenderer;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import me.zyouime.holymoderation.render.utils.CRenderPipelines;
import me.zyouime.holymoderation.render.utils.UniformBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Data
public final class BuiltBlur implements IRenderer {

    private SizeState size;
    private QuadRadiusState radius;
    private QuadColorState color;
    private float smoothness;
    private float blurRadius;
    private static GpuTexture TEMP_TEXTURE = null;
    private static GpuTextureView TEMP_VIEW = null;
    private static final UniformBuffer BLUR_BUFFER = new UniformBuffer("blur_buffer", new Std140SizeCalculator()
            .putVec4()
            .putVec2()
            .putFloat()
            .putFloat()
            .get());

    public BuiltBlur(SizeState size, QuadRadiusState radius, QuadColorState color, float smoothness, float blurRadius) {
        this.size = size;
        this.radius = radius;
        this.color = color;
        this.smoothness = smoothness;
        this.blurRadius = blurRadius;
    }

    private static void prepareTempTexture() {
        Framebuffer fbo = MinecraftClient.getInstance().getFramebuffer();
        if (TEMP_TEXTURE == null || TEMP_TEXTURE.getWidth(0) != fbo.textureWidth || TEMP_TEXTURE.getHeight(0) != fbo.textureHeight) {
            if (TEMP_TEXTURE != null) {
                TEMP_VIEW.close();
                TEMP_TEXTURE.close();
            }
            TEMP_TEXTURE = RenderSystem.getDevice().createTexture(() -> "blur_temp",
                    GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST,
                    TextureFormat.RGBA8,
                    fbo.textureWidth,
                    fbo.textureHeight,
                    1,
                    1);
            TEMP_VIEW = RenderSystem.getDevice().createTextureView(TEMP_TEXTURE);
        }
        RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(
                fbo.getColorAttachment(),
                TEMP_TEXTURE,
                0,
                0, 0,
                0, 0,
                fbo.textureWidth,
                fbo.textureHeight);
    }

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        prepareTempTexture();
        float width = this.size.width(), height = this.size.height();
		BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(this.color.color1());
        builder.vertex(matrix, x, y + height, z).color(this.color.color2());
        builder.vertex(matrix, x + width, y + height, z).color(this.color.color3());
        builder.vertex(matrix, x + width, y, z).color(this.color.color4());
        BuiltBuffer buffer = builder.end();
        GpuBufferSlice blurUniforms = BLUR_BUFFER.write(writer -> {
            writer.putVec4(new Vector4f(this.radius.radius1(), this.radius.radius2(), this.radius.radius3(), this.radius.radius4()));
            writer.putVec2(new Vector2f(width, height));
            writer.putFloat(this.smoothness);
            writer.putFloat(this.blurRadius);
        });
        RenderPass renderPass = BufferRenderer.uploadBuffer(buffer);
        renderPass.setPipeline(CRenderPipelines.BLUR_PIPLINE);
        renderPass.setUniform("BlurUniforms", blurUniforms);
        renderPass.bindTexture("Sampler0", TEMP_VIEW, RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
        BufferRenderer.renderBuffer(buffer, renderPass);
    }

}