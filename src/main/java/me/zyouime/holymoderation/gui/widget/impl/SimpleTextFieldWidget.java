package me.zyouime.holymoderation.gui.widget.impl;

import lombok.Getter;
import lombok.Setter;
import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltBorder;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.render.utils.ScissorStack;
import me.zyouime.holymoderation.resources.Fonts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.StringHelper;
import org.joml.Matrix4fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.function.Consumer;

public class SimpleTextFieldWidget extends AbstractElement {

    private static final int DEFAULT_MAX_LENGTH = 256;
    @Setter
    private Consumer<String> callback;
    @Getter
    private String text;
    @Setter
    private int maxLength = DEFAULT_MAX_LENGTH;
    @Setter
    private String placeholder = "";
    @Getter
    @Setter
    private boolean focused;
    private final Setting<String> setting;
    private int firstCharIndex;
    private boolean allSelected;
    private double cursorBlinkStart = System.currentTimeMillis();
    private final BuiltBorder border;
    private final BuiltRectangle background;
    private final BuiltRectangle selection;
    private final BuiltRectangle cursor;
    private final BuiltText textRender;

    public SimpleTextFieldWidget(float x, float y, float width, float height, Setting<String> setting) {
        super(x, y, width, height);
        this.setting = setting;
        this.text = setting != null && setting.getValue() != null ? setting.getValue() : "";
        this.border = Builder.border()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(4.0f))
                .color(new QuadColorState(GuiColors.BORDER))
                .thickness(0.06f)
                .build();
        this.background = Builder.rectangle()
                .size(new SizeState(width - 2.0f, height - 2.0f))
                .radius(new QuadRadiusState(3.0f))
                .color(new QuadColorState(GuiColors.ELEMENT_BACK))
                .build();
        this.selection = Builder.rectangle()
                .size(SizeState.NONE)
                .radius(new QuadRadiusState(1.0f))
                .color(new QuadColorState(GuiColors.ACCENT_DIM))
                .build();
        this.cursor = Builder.rectangle()
                .size(new SizeState(1.0f, height * 0.55f))
                .radius(QuadRadiusState.NO_ROUND)
                .color(new QuadColorState(Color.WHITE))
                .build();
        this.textRender = Builder.text()
                .text(this.text)
                .color(GuiColors.TEXT)
                .font(Fonts.UI.get())
                .thickness(0.05f)
                .size(height / 2.6f)
                .build();
        this.scrollToEnd();
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
        this.scrollToEnd();
    }

    private float innerWidth() {
        return this.width - 8.0f;
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.border.setSize(new SizeState(this.width, this.height));
        this.background.setSize(new SizeState(this.width - 2.0f, this.height - 2.0f));
        this.border.setColor(new QuadColorState(this.focused ? GuiColors.ACCENT : GuiColors.DIVIDER));
        this.border.render(matrices, this.x, this.y);
        this.background.render(matrices, this.x + 1.0f, this.y + 1.0f);
        this.firstCharIndex = Math.min(this.firstCharIndex, this.text.length());
        String visible = this.text.substring(this.firstCharIndex);
        boolean empty = this.text.isEmpty();
        this.textRender.setText(empty ? this.placeholder : visible);
        this.textRender.setColor(empty ? GuiColors.TEXT_FAINT : (this.focused ? GuiColors.TEXT : GuiColors.withAlpha(GuiColors.TEXT, 190)));
        float textX = this.x + 4.0f;
        float textY = this.y + (this.height - this.textRender.getLineHeight()) / 2.0f;
        ScissorStack.push(this.x + 2.0f, this.y + 1.0f, this.x + this.width - 2.0f, this.y + this.height - 1.0f);
        try {
            if (this.allSelected && this.focused && !empty) {
                this.selection.setSize(new SizeState(this.textRender.measureWidth(visible) + 2.0f, this.height * 0.6f));
                this.selection.render(matrices, textX - 1.0f, this.y + this.height * 0.2f);
            }
            this.textRender.render(matrices, textX, textY);
            if (this.focused && this.isCursorVisible()) {
                this.cursor.setSize(new SizeState(1.0f, this.height * 0.55f));
                this.cursor.render(matrices, textX + this.textRender.measureWidth(visible) + 1.0f, this.y + this.height * 0.22f);
            }
        } finally {
            ScissorStack.pop();
        }
    }

    private boolean isCursorVisible() {
        return ((System.currentTimeMillis() - this.cursorBlinkStart) / 500L) % 2L == 0L;
    }

    private void resetCursorBlink() {
        this.cursorBlinkStart = System.currentTimeMillis();
    }

    @Override
    public void resetSetting() {
        if (this.setting == null) {
            return;
        }
        this.setting.reset();
        this.setText(this.setting.getValue());
        this.onChanged();
    }

    public void onChanged() {
        if (this.callback != null) {
            this.callback.accept(this.text);
        }
    }

    private static boolean isAllowed(char chr) {
        return chr == '§' || StringHelper.isValidChar(chr);
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char chr = value.charAt(i);
            if (isAllowed(chr)) {
                builder.append(chr);
            }
        }
        return builder.toString();
    }

    private void insert(String value) {
        if (this.allSelected) {
            this.text = "";
            this.allSelected = false;
        }
        int room = this.maxLength - this.text.length();
        if (room <= 0) {
            return;
        }
        this.text += value.length() > room ? value.substring(0, room) : value;
        this.scrollToEnd();
        this.onChanged();
        this.resetCursorBlink();
    }

    @Override
    public boolean keyPressed(int keyCode, int modifiers) {
        if (!this.focused || !this.active) {
            return false;
        }
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_A -> {
                    this.allSelected = true;
                    return true;
                }
                case GLFW.GLFW_KEY_C -> {
                    MinecraftClient.getInstance().keyboard.setClipboard(this.text);
                    return true;
                }
                case GLFW.GLFW_KEY_X -> {
                    MinecraftClient.getInstance().keyboard.setClipboard(this.text);
                    if (this.allSelected) {
                        this.text = "";
                        this.allSelected = false;
                        this.scrollToEnd();
                        this.onChanged();
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_V -> {
                    this.insert(sanitize(MinecraftClient.getInstance().keyboard.getClipboard()));
                    return true;
                }
                default -> {
                }
            }
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                this.focused = false;
                this.allSelected = false;
                return true;
            }
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (this.allSelected) {
                    this.text = "";
                    this.allSelected = false;
                } else if (!this.text.isEmpty()) {
                    this.text = this.text.substring(0, this.text.length() - 1);
                }
                this.scrollToStart();
                this.onChanged();
                this.resetCursorBlink();
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.active || !this.focused) {
            return false;
        }
        if (!isAllowed(chr)) {
            return false;
        }
        this.insert(String.valueOf(chr));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.isOverAndActive(mouseX, mouseY)) {
            this.focused = true;
            this.allSelected = false;
            this.resetCursorBlink();
            ModSounds.playClick();
            return true;
        }
        this.focused = false;
        this.allSelected = false;
        return false;
    }

    private void scrollToEnd() {
        this.firstCharIndex = Math.min(this.firstCharIndex, this.text.length());
        float max = this.innerWidth();
        for (int i = this.firstCharIndex; i <= this.text.length(); i++) {
            if (this.textRender.measureWidth(this.text.substring(i)) <= max) {
                this.firstCharIndex = i;
                return;
            }
        }
        this.firstCharIndex = this.text.length();
    }

    private void scrollToStart() {
        this.firstCharIndex = Math.min(this.firstCharIndex, this.text.length());
        float max = this.innerWidth();
        for (int i = this.firstCharIndex; i > 0; i--) {
            if (this.textRender.measureWidth(this.text.substring(i - 1)) > max) {
                this.firstCharIndex = i;
                return;
            }
        }
        this.firstCharIndex = 0;
    }
}
