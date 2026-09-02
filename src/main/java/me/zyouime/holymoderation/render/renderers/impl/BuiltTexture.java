package me.zyouime.holymoderation.render.renderers.impl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import lombok.Data;
import me.zyouime.holymoderation.render.renderers.IRenderer;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import me.zyouime.holymoderation.render.utils.CRenderPipelines;
import me.zyouime.holymoderation.render.utils.UniformBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Data
public final class BuiltTexture implements IRenderer {

    private SizeState size;
    private QuadRadiusState radius;
    private QuadColorState color;
    private float smoothness;
    private float u, v;
    private float texWidth, texHeight;
    private AbstractTexture glTexture;
    private static final UniformBuffer TEXTURE_BUFFER = new UniformBuffer("texture_buffer", new Std140SizeCalculator()
            .putVec4()
            .putVec2()
            .putFloat().get());

    public BuiltTexture(SizeState size, QuadRadiusState radius, QuadColorState color, float smoothness, float u, float v, float texWidth, float texHeight, AbstractTexture glTexture) {
        this.size = size;
        this.radius = radius;
        this.color = color;
        this.smoothness = smoothness;
        this.u = u;
        this.v = v;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.glTexture = glTexture;
    }
    
    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        float width = this.size.width(), height = this.size.height();
        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(matrix, x, y, z).texture(this.u, this.v).color(this.color.color1());
        builder.vertex(matrix, x, y + height, z).texture(this.u, this.v + this.texHeight).color(this.color.color2());
        builder.vertex(matrix, x + width, y + height, z).texture(this.u + this.texWidth, this.v + this.texHeight).color(this.color.color3());
        builder.vertex(matrix, x + width, y, z).texture(this.u + this.texWidth, this.v).color(this.color.color4());
        BuiltBuffer buffer = builder.end();
        GpuBufferSlice textureUniforms = TEXTURE_BUFFER.write(writer -> {
            writer.putVec4(new Vector4f(this.radius.radius1(), this.radius.radius2(), this.radius.radius3(), this.radius.radius4()));
            writer.putVec2(new Vector2f(width, height));
            writer.putFloat(this.smoothness);
        });
        RenderPass renderPass = BufferRenderer.uploadBuffer(buffer);
        renderPass.setPipeline(CRenderPipelines.TEXTURE_PIPLINE);
        renderPass.setUniform("TextureUniforms", textureUniforms);
        renderPass.bindTexture("Sampler0", this.glTexture.getGlTextureView(), this.glTexture.getSampler());
        BufferRenderer.renderBuffer(buffer, renderPass);
    }

}