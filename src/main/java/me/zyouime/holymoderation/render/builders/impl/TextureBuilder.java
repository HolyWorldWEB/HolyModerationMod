package me.zyouime.holymoderation.render.builders.impl;

import me.zyouime.holymoderation.render.builders.AbstractBuilder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltTexture;
import net.minecraft.client.texture.AbstractTexture;

public final class TextureBuilder extends AbstractBuilder<BuiltTexture> {

    private SizeState size;
    private QuadRadiusState radius;
    private QuadColorState color;
    private float smoothness;
    private float u, v;
    private float texWidth, texHeight;
    private AbstractTexture glTexture;

    public TextureBuilder size(SizeState size) {
        this.size = size;
        return this;
    }

    public TextureBuilder radius(QuadRadiusState radius) {
        this.radius = radius;
        return this;
    }

    public TextureBuilder color(QuadColorState color) {
        this.color = color;
        return this;
    }

    public TextureBuilder smoothness(float smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    public TextureBuilder texture(float u, float v, float texWidth, float texHeight, AbstractTexture glTexture) {
        this.u = u;
        this.v = v;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.glTexture = glTexture;
        return this;
    }

    @Override
    protected BuiltTexture _build() {
        return new BuiltTexture(
            this.size,
            this.radius,
            this.color,
            this.smoothness,
            this.u, this.v,
            this.texWidth, this.texHeight,
            this.glTexture
        );
    }

    @Override
    protected void reset() {
        this.size = SizeState.NONE;
        this.radius = QuadRadiusState.NO_ROUND;
        this.color = QuadColorState.WHITE;
        this.smoothness = 1.0f;
        this.u = 0.0f;
        this.v = 0.0f;
        this.texWidth = 0.0f;
        this.texHeight = 0.0f;
        this.glTexture = null;
    }

}