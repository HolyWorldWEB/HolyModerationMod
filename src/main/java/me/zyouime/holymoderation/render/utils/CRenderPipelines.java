package me.zyouime.holymoderation.render.utils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.render.providers.ResourceProvider;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class CRenderPipelines {

    private static Identifier location(String path) {
        return Identifier.of(Main.MOD_ID, path);
    }

    public static final RenderPipeline RECTANGLE_PIPLINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(location("pipeline/rectangle"))
                    .withUniform("RectangleUniforms", UniformType.UNIFORM_BUFFER)
                    .withVertexShader(ResourceProvider.getShaderIdentifier("rectangle"))
                    .withFragmentShader(ResourceProvider.getShaderIdentifier("rectangle"))
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS).build()
    );

    public static final RenderPipeline BORDER_PIPLINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(location("pipeline/border"))
                    .withUniform("BorderUniforms", UniformType.UNIFORM_BUFFER)
                    .withVertexShader(ResourceProvider.getShaderIdentifier("border"))
                    .withFragmentShader(ResourceProvider.getShaderIdentifier("border"))
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS).build()
    );

    public static final RenderPipeline TEXTURE_PIPLINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(location("pipeline/texture"))
                    .withUniform("TextureUniforms", UniformType.UNIFORM_BUFFER)
                    .withSampler("Sampler0")
                    .withVertexShader(ResourceProvider.getShaderIdentifier("texture"))
                    .withFragmentShader(ResourceProvider.getShaderIdentifier("texture"))
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS).build()
    );

    public static final RenderPipeline MSDF_FONT_PIPLINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(location("pipeline/msdf_font"))
                    .withUniform("TextUniforms", UniformType.UNIFORM_BUFFER)
                    .withSampler("Sampler0")
                    .withVertexShader(ResourceProvider.getShaderIdentifier("msdf_font"))
                    .withFragmentShader(ResourceProvider.getShaderIdentifier("msdf_font"))
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, DrawMode.QUADS).build()
    );

    public static final RenderPipeline BLUR_PIPLINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(location("pipeline/blur"))
                    .withUniform("BlurUniforms", UniformType.UNIFORM_BUFFER)
                    .withSampler("Sampler0")
                    .withVertexShader(ResourceProvider.getShaderIdentifier("blur"))
                    .withFragmentShader(ResourceProvider.getShaderIdentifier("blur"))
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.QUADS).build()
    );
}