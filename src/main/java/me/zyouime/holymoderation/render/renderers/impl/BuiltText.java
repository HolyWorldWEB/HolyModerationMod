package me.zyouime.holymoderation.render.renderers.impl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import lombok.Data;
import me.zyouime.holymoderation.render.msdf.MsdfFont;
import me.zyouime.holymoderation.render.providers.ColorProvider;
import me.zyouime.holymoderation.render.renderers.IRenderer;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import me.zyouime.holymoderation.render.utils.CRenderPipelines;
import me.zyouime.holymoderation.render.utils.UniformBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.Color;

@Data
public final class BuiltText implements IRenderer {

    private static final String ELLIPSIS = "...";
    private MsdfFont font;
    private String text;
    private float size;
    private float thickness;
    private int color;
    private float smoothness;
    private float spacing;
    private int outlineColor;
    private float outlineThickness;
    private float maxWidth;
    private String cacheSource;
    private float cacheMaxWidth = -1.0f;
    private float cacheSize = -1.0f;
    private String cacheResult;

    private static final UniformBuffer TEXT_BUFFER = new UniformBuffer("text_buffer", new Std140SizeCalculator()
            .putVec4()
            .putFloat()
            .putFloat()
            .putFloat()
            .putFloat()
            .putInt()
            .get());

    public BuiltText(MsdfFont font, String text, float size, float thickness, int color, float smoothness, float spacing, int outlineColor, float outlineThickness, float maxWidth) {
        this.font = font;
        this.text = text;
        this.size = size;
        this.thickness = thickness;
        this.color = color;
        this.smoothness = smoothness;
        this.spacing = spacing;
        this.outlineColor = outlineColor;
        this.outlineThickness = outlineThickness;
        this.maxWidth = maxWidth;
    }

    public void setColor(Color color) {
        this.color = color.getRGB();
    }

    private float extraPerGlyph() {
        return (this.thickness + this.outlineThickness * 0.5f) * 0.5f * this.size + this.spacing;
    }

    public float measureWidth(String value) {
        if (this.font == null || value == null || value.isEmpty()) {
            return 0.0f;
        }
        return this.font.getWidth(value, this.size, this.extraPerGlyph());
    }

    public float measureWidth() {
        return measureWidth(displayText());
    }

    public float getTextWidth() {
        return this.font.getWidth(displayText(), size);
    }

    public float getLineHeight() {
        return this.font == null ? 0.0f : this.font.getMetrics().lineHeight() * this.size;
    }

    public String displayText() {
        String source = this.text == null ? "" : this.text;
        if (this.maxWidth <= 0.0f || this.font == null) {
            return source;
        }
        if (source.equals(this.cacheSource) && this.cacheMaxWidth == this.maxWidth && this.cacheSize == this.size) {
            return this.cacheResult;
        }
        String result = source;
        if (this.measureWidth(source) > this.maxWidth) {
            result = ELLIPSIS;
            for (int i = source.length() - 1; i > 0; i--) {
                String candidate = source.substring(0, i) + ELLIPSIS;
                if (this.measureWidth(candidate) <= this.maxWidth) {
                    result = candidate;
                    break;
                }
            }
        }
        this.cacheSource = source;
        this.cacheMaxWidth = this.maxWidth;
        this.cacheSize = this.size;
        this.cacheResult = result;
        return result;
    }

    public void renderCenteredX(Matrix4f matrix, float centerX, float y) {
        this.render(matrix, centerX - this.getTextWidth() / 2.0f, y);
    }

    public void renderCenteredY(Matrix4f matrix, float x, float centerY) {
        this.render(matrix, x, centerY - this.getLineHeight() / 2.0f);
    }

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        String rendered = this.displayText();
        if (this.font == null || rendered.isEmpty()) {
            return;
        }
        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        this.font.applyGlyphs(matrix, builder, rendered, this.size,
                (this.thickness + this.outlineThickness * 0.5f) * 0.5f * this.size, this.spacing,
                x, y + this.font.getMetrics().baselineHeight() * this.size, z, this.color);

        BuiltBuffer buffer = builder.end();
        boolean outlineEnabled = (this.outlineThickness > 0.0f);
        GpuBufferSlice uniforms = TEXT_BUFFER.write(writer -> {
            float[] outlineComponents = ColorProvider.normalize(this.outlineColor);
            writer.putVec4(new Vector4f(outlineComponents[0], outlineComponents[1], outlineComponents[2], outlineComponents[3]));
            writer.putFloat(this.font.getAtlas().range());
            writer.putFloat(this.thickness);
            writer.putFloat(this.smoothness);
            writer.putFloat(this.outlineThickness);
            writer.putInt(outlineEnabled ? 1 : 0);
        });
        RenderPass renderPass = BufferRenderer.uploadBuffer(buffer);
        renderPass.setPipeline(CRenderPipelines.MSDF_FONT_PIPLINE);
        renderPass.setUniform("TextUniforms", uniforms);
        renderPass.bindTexture("Sampler0", this.font.getTexture().getGlTextureView(), this.font.getTexture().getSampler());
        BufferRenderer.renderBuffer(buffer, renderPass);
    }
}
