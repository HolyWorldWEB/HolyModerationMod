package me.zyouime.holymoderation.render.utils;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.function.Consumer;

public final class UniformBuffer implements AutoCloseable {

    private static final int SLOT_ALIGNMENT = 256;
    private static final int SLOT_COUNT = 512;
    private final String name;
    private final int stride;
    private GpuBuffer buffer;
    private int slot;

    public UniformBuffer(String name, int size) {
        this.name = name;
        this.stride = Math.max(SLOT_ALIGNMENT, ((size + SLOT_ALIGNMENT - 1) / SLOT_ALIGNMENT) * SLOT_ALIGNMENT);
    }

    private GpuBuffer buffer() {
        if (this.buffer == null) {
            this.buffer = RenderSystem.getDevice().createBuffer(() -> this.name, GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE, (long) this.stride * SLOT_COUNT);
        }
        return this.buffer;
    }

    public GpuBufferSlice write(Consumer<Std140Builder> writer) {
        GpuBuffer buf = this.buffer();
        int offset = this.slot * this.stride;
        this.slot = (this.slot + 1) % SLOT_COUNT;
        GpuBufferSlice slice = buf.slice(offset, this.stride);
        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(slice, false, true)) {
            writer.accept(Std140Builder.intoBuffer(view.data()));
        }
        return slice;
    }

    @Override
    public void close() {
        if (this.buffer != null) {
            this.buffer.close();
            this.buffer = null;
        }
        this.slot = 0;
    }
}
