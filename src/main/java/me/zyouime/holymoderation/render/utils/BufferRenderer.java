package me.zyouime.holymoderation.render.utils;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.OptionalInt;

public final class BufferRenderer {

    private static final Deque<Float> ALPHA_STACK = new ArrayDeque<>();
    private static float alpha = 1.0f;

    public static void pushAlpha(float multiplier) {
        ALPHA_STACK.push(alpha);
        alpha = MathHelper.clamp(alpha * multiplier, 0.0f, 1.0f);
    }

    public static void popAlpha() {
        alpha = ALPHA_STACK.isEmpty() ? 1.0f : ALPHA_STACK.pop();
    }

    public static float alpha() {
        return alpha;
    }

    public static void resetAlpha() {
        ALPHA_STACK.clear();
        alpha = 1.0f;
    }

    public static RenderPass uploadBuffer(BuiltBuffer buffer) {
        VertexFormat vertexFormat = buffer.getDrawParameters().format();
        GpuBuffer vertexBuffer = vertexFormat.uploadImmediateVertexBuffer(buffer.getBuffer());
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(buffer.getDrawParameters().mode());
        GpuBuffer indexBuffer = shapeIndexBuffer.getIndexBuffer(buffer.getDrawParameters().indexCount());
        VertexFormat.IndexType indexType = shapeIndexBuffer.getIndexType();
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().write(
                new Matrix4f().setTranslation(0.0f, 0.0f, -11000.0f),
                new Vector4f(1.0f, 1.0f, 1.0f, alpha),
                new Vector3f(),
                new Matrix4f());
        GpuBufferSlice projection = RenderSystem.getProjectionMatrixBuffer();
        Framebuffer fbo = MinecraftClient.getInstance().getFramebuffer();
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "holymoderation", fbo.getColorAttachmentView(), OptionalInt.empty());
        renderPass.setVertexBuffer(0, vertexBuffer);
        renderPass.setIndexBuffer(indexBuffer, indexType);
        renderPass.setUniform("Projection", projection);
        renderPass.setUniform("DynamicTransforms", transforms);
        ScissorStack.apply(renderPass);
        return renderPass;
    }

    public static void renderBuffer(BuiltBuffer buffer, RenderPass renderPass) {
        renderPass.drawIndexed(0, 0, buffer.getDrawParameters().indexCount(), 1);
        renderPass.close();
        buffer.close();
    }
}
