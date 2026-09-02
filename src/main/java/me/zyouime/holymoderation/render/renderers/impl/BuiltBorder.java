package me.zyouime.holymoderation.render.renderers.impl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import lombok.Data;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.IRenderer;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import me.zyouime.holymoderation.render.utils.CRenderPipelines;
import me.zyouime.holymoderation.render.utils.UniformBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

@Data
public final class BuiltBorder implements IRenderer {

    private SizeState size;
    private QuadRadiusState radius;
    private QuadColorState color;
    private float thickness;
    private float internalSmoothness, externalSmoothness;
    private static final UniformBuffer BORDER_BUFFER = new UniformBuffer("border_buffer", new Std140SizeCalculator()
            .putVec4()
            .putVec2()
            .putVec2()
            .putFloat().get());

    public BuiltBorder(SizeState size, QuadRadiusState radius, QuadColorState color, float thickness, float internalSmoothness, float externalSmoothness) {
        this.size = size;
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
        this.internalSmoothness = internalSmoothness;
        this.externalSmoothness = externalSmoothness;
    }

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        float width = this.size.width(), height = this.size.height();

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(this.color.color1());
        builder.vertex(matrix, x, y + height, z).color(this.color.color2());
        builder.vertex(matrix, x + width, y + height, z).color(this.color.color3());
        builder.vertex(matrix, x + width, y, z).color(this.color.color4());

        BuiltBuffer buffer = builder.end();
        GpuBufferSlice borderUniforms = BORDER_BUFFER.write(writer -> {
            writer.putVec4(new Vector4f(this.radius.radius1(), this.radius.radius2(), this.radius.radius3(), this.radius.radius4()));
            writer.putVec2(new Vector2f(width, height));
            writer.putVec2(new Vector2f(internalSmoothness, externalSmoothness));
            writer.putFloat(this.thickness);
        });
        RenderPass renderPass = BufferRenderer.uploadBuffer(buffer);
        renderPass.setPipeline(CRenderPipelines.BORDER_PIPLINE);
        renderPass.setUniform("BorderUniforms", borderUniforms);
        BufferRenderer.renderBuffer(buffer, renderPass);
    }

}