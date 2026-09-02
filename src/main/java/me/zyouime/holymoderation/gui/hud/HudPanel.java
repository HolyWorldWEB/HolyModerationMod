package me.zyouime.holymoderation.gui.hud;

import java.util.ArrayList;
import java.util.List;

import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.settings.SettingsCatalog;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.render.animation.Animation;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import me.zyouime.holymoderation.resources.Fonts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;

public abstract class HudPanel {

    protected static final float BASE_WIDTH = 150.0f;
    private static final float PADDING = 9.0f;
    private static final float RADIUS = 8.0f;
    private static final float BADGE = 20.0f;
    private static final float BADGE_RADIUS = 6.0f;
    private static final float BADGE_GAP = 9.0f;
    private static final float TITLE_SIZE = 8.5f;
    private static final float TEXT_SIZE = 7.5f;
    private static final float LINE_GAP = 2.5f;
    private static final float DOT = 3.5f;
    private static final float DOT_GAP = 4.0f;
    private static final float SPAWN_SECONDS = 0.28f;
    private static final float HIDE_SECONDS = 0.32f;
    private static final float RISE = 6.0f;
    private static final BuiltRectangle RECT = Builder.rectangle()
            .size(SizeState.NONE)
            .radius(new QuadRadiusState(RADIUS))
            .color(QuadColorState.WHITE)
            .build();
    private static final BuiltText TITLE = Builder.text()
            .text("")
            .size(TITLE_SIZE)
            .color(GuiColors.TEXT)
            .thickness(0.05f)
            .font(Fonts.UI.get())
            .build();
    private static final BuiltText TEXT = Builder.text()
            .text("")
            .size(TEXT_SIZE)
            .color(GuiColors.TEXT_MUTED)
            .thickness(0.05f)
            .font(Fonts.UI.get())
            .build();
    private final BuiltText icon;
    private final List<HudLine> lines = new ArrayList<>(3);
    private String title = "";
    private float phase;
    private boolean dragging;
    private float grabX;
    private float grabY;
    private float lastX;
    private float lastY;
    private float lastWidth = BASE_WIDTH;
    private float lastHeight = 40.0f;

    protected HudPanel(String iconGlyph) {
        this.icon = Builder.text()
                .text(iconGlyph)
                .size(10.0f)
                .color(GuiColors.ON_ACCENT)
                .font(Fonts.ICONS.get())
                .build();
    }

    protected abstract HudBinding binding();

    protected abstract String placeholder();

    protected abstract String idleText();

    protected abstract boolean snapshot();

    protected final void content(String title, HudLine... lines) {
        this.title = title;
        this.lines.clear();
        this.lines.addAll(List.of(lines));
    }

    public final void reset() {
        title = "";
        lines.clear();
    }

    public final void resetPosition() {
        binding().x().reset();
        binding().y().reset();
    }

    public final float scale() {
        Integer percent = binding().scale().getValue();
        int value = percent == null ? 100 : percent;
        return MathHelper.clamp(value, SettingsCatalog.HUD_SCALE_MIN, SettingsCatalog.HUD_SCALE_MAX) / 100.0f;
    }

    public final void render(Matrix4fStack matrices) {
        MinecraftClient client = MinecraftProvider.client();
        if (client.options != null && client.options.hudHidden) {
            return;
        }
        if (MinecraftProvider.world() == null) {
            return;
        }
        boolean snapshot = snapshot();
        boolean visible = binding().enabled().getValue() && (snapshot || isChatScreen());
        if (!snapshot) {
            reset();
        }
        float step = Animation.frameDelta() / (visible ? SPAWN_SECONDS : HIDE_SECONDS);
        phase = MathHelper.clamp(phase + (visible ? step : -step), 0.0f, 1.0f);
        if (phase <= 0.0f) {
            return;
        }
        float scale = scale();
        float height = measureHeight();
        Window window = client.getWindow();
        float screenWidth = window.getScaledWidth() / scale;
        float screenHeight = window.getScaledHeight() / scale;
        float x = position(binding().x().getValue()) * Math.max(0.0f, screenWidth - BASE_WIDTH);
        float y = position(binding().y().getValue()) * Math.max(0.0f, screenHeight - height);
        lastX = x * scale;
        lastY = y * scale;
        lastWidth = BASE_WIDTH * scale;
        lastHeight = height * scale;
        float eased = easeOut(phase);
        matrices.pushMatrix();
        matrices.scale(scale, scale, 1.0f);
        BufferRenderer.pushAlpha(eased);
        try {
            draw(matrices, x, y + (1.0f - eased) * RISE, height);
        } finally {
            BufferRenderer.popAlpha();
            matrices.popMatrix();
        }
    }

    private static float position(Float value) {
        return MathHelper.clamp(value == null ? 0.0f : value, 0.0f, 1.0f);
    }

    private static float easeOut(float t) {
        float inverted = 1.0f - t;
        return 1.0f - inverted * inverted * inverted;
    }

    public static boolean isChatScreen() {
        return MinecraftProvider.currentScreen() instanceof ChatScreen;
    }

    private float contentHeight() {
        float total = TITLE.getLineHeight();
        int count = Math.max(1, lines.size());
        for (int index = 0; index < count; index++) {
            total += LINE_GAP + TEXT.getLineHeight();
        }
        return total;
    }

    private float measureHeight() {
        return PADDING * 2.0f + Math.max(BADGE, contentHeight());
    }

    private void draw(Matrix4fStack matrices, float x, float y, float height) {
        RECT.setRadius(new QuadRadiusState(RADIUS));
        RECT.setSize(new SizeState(BASE_WIDTH, height));
        RECT.setColor(new QuadColorState(isChatScreen() ? GuiColors.ACCENT : GuiColors.BORDER));
        RECT.render(matrices, x, y);
        RECT.setSize(new SizeState(BASE_WIDTH - 2.0f, height - 2.0f));
        RECT.setColor(new QuadColorState(GuiColors.CARD));
        RECT.render(matrices, x + 1.0f, y + 1.0f);
        RECT.setRadius(new QuadRadiusState(BADGE_RADIUS));
        RECT.setSize(new SizeState(BADGE, BADGE));
        RECT.setColor(new QuadColorState(GuiColors.ACCENT));
        RECT.render(matrices, x + PADDING, y + (height - BADGE) / 2.0f);
        icon.render(matrices, x + PADDING + 5.0f, y + (height - BADGE) / 2.0f + 5.0f);
        float textX = x + PADDING + BADGE + BADGE_GAP;
        float maxWidth = BASE_WIDTH - (textX - x) - PADDING;
        float textY = y + (height - contentHeight()) / 2.0f;
        TITLE.setMaxWidth(maxWidth);
        TITLE.setText(title.isEmpty() ? placeholder() : title);
        TITLE.render(matrices, textX, textY);
        textY += TITLE.getLineHeight() + LINE_GAP;
        if (lines.isEmpty()) {
            drawLine(matrices, HudLine.dotted(idleText(), GuiColors.TEXT_FAINT, GuiColors.TEXT_MUTED), textX, textY, maxWidth);
            return;
        }
        for (HudLine line : lines) {
            drawLine(matrices, line, textX, textY, maxWidth);
            textY += TEXT.getLineHeight() + LINE_GAP;
        }
    }

    private void drawLine(Matrix4fStack matrices, HudLine line, float textX, float textY, float maxWidth) {
        float offset = 0.0f;
        if (line.dot() != null) {
            RECT.setRadius(new QuadRadiusState(DOT / 2.0f));
            RECT.setSize(new SizeState(DOT, DOT));
            RECT.setColor(new QuadColorState(line.dot()));
            RECT.render(matrices, textX, textY + (TEXT.getLineHeight() - DOT) / 2.0f);
            offset = DOT + DOT_GAP;
        }
        TEXT.setMaxWidth(maxWidth - offset);
        TEXT.setColor(line.color());
        TEXT.setText(line.text());
        TEXT.render(matrices, textX + offset, textY);
    }

    public final boolean isMouseOver(double mouseX, double mouseY) {
        return phase > 0.0f && mouseX >= lastX && mouseX <= lastX + lastWidth && mouseY >= lastY && mouseY <= lastY + lastHeight;
    }

    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !isMouseOver(mouseX, mouseY)) {
            return false;
        }
        dragging = true;
        grabX = (float) mouseX - lastX;
        grabY = (float) mouseY - lastY;
        return true;
    }

    public final boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!dragging) {
            return false;
        }
        Window window = MinecraftProvider.client().getWindow();
        float freeX = Math.max(1.0f, window.getScaledWidth() - lastWidth);
        float freeY = Math.max(1.0f, window.getScaledHeight() - lastHeight);
        binding().x().setValue(MathHelper.clamp(((float) mouseX - grabX) / freeX, 0.0f, 1.0f));
        binding().y().setValue(MathHelper.clamp(((float) mouseY - grabY) / freeY, 0.0f, 1.0f));
        return true;
    }

    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!dragging) {
            return false;
        }
        dragging = false;
        return true;
    }
}