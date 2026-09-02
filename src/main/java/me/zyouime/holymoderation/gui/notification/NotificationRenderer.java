package me.zyouime.holymoderation.gui.notification;

import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.core.notification.Notification;
import me.zyouime.holymoderation.core.notification.NotificationType;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.settings.SettingsCatalog;
import me.zyouime.holymoderation.render.animation.Animation;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.text.FormattedText;
import me.zyouime.holymoderation.render.text.FormattedText.Run;
import me.zyouime.holymoderation.render.text.RichTextRenderer;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NotificationRenderer {

    public static final float BASE_WIDTH = 178.0f;
    private static final float MARGIN = 10.0f;
    private static final float GAP = 6.0f;
    private static final float PADDING = 10.0f;
    private static final float RADIUS = 9.0f;
    private static final float ICON = 22.0f;
    private static final float ICON_RADIUS = 7.0f;
    private static final float ICON_GAP = 10.0f;
    private static final float GLYPH_THICKNESS = 2.0f;
    private static final float TITLE_SIZE = 8.5f;
    private static final float TEXT_SIZE = 7.5f;
    private static final float LINE_GAP = 1.5f;
    private static final float PROGRESS_HEIGHT = 2.0f;
    private static final float SLIDE = BASE_WIDTH + MARGIN + 4.0f;
    private static final float TRAVEL_WINDOW = 0.8f;
    private static final float FADE_TAIL = 0.22f;
    private static final float DOT_SIZE = 3.2f;
    private static final float SPAWN_SECONDS = 0.30f;
    private static final float HIDE_SECONDS = 0.45f;
    private static final float TICKS_PER_SECOND = 20.0f;
    private static final Color CARD = new Color(36, 29, 23, 245);
    private static final Color CARD_EDGE = new Color(52, 42, 33, 255);
    private static final Color TITLE_COLOR = new Color(244, 237, 229);
    private static final Color TEXT_COLOR = new Color(158, 146, 136);
    private static final Color GLYPH = new Color(28, 21, 16);
    private static final Map<Long, Card> CARDS = new LinkedHashMap<>();
    private static final RichTextRenderer TITLE = new RichTextRenderer(TITLE_SIZE);
    private static final RichTextRenderer BODY = new RichTextRenderer(TEXT_SIZE);
    private static final BuiltRectangle RECT = Builder.rectangle()
            .size(SizeState.NONE)
            .radius(new QuadRadiusState(RADIUS))
            .color(QuadColorState.WHITE)
            .build();
    private static final BuiltRectangle BAR = Builder.rectangle()
            .size(SizeState.NONE)
            .radius(new QuadRadiusState(1.0f))
            .color(QuadColorState.WHITE)
            .build();

    private static final class Card {

        private NotificationType type;
        private String title;
        private String text;
        private boolean persistent;
        private int lifeTicks;
        private boolean present;
        private boolean leaving;
        private float phase;
        private float y;
        private boolean placed;
        private int lastElapsedTicks = -1;
        private float subTick;
        private float lifeLeft = 1.0f;
    }

    public static float scale() {
        if (Main.getModContext() == null) {
            return 1.0f;
        }
        Integer percent = Main.getModContext().settings().notificationScale.getValue();
        int value = percent == null ? 100 : percent;
        return MathHelper.clamp(value, SettingsCatalog.NOTIFICATION_SCALE_MIN, SettingsCatalog.NOTIFICATION_SCALE_MAX) / 100.0f;
    }

    public static void render(Matrix4fStack matrices, float delta) {
        MinecraftClient client = MinecraftProvider.client();
        if (client.options != null && client.options.hudHidden) {
            return;
        }
        sync(Main.getModContext().notificationsService().notifications());
        if (CARDS.isEmpty()) {
            return;
        }
        float scale = scale();
        float right = client.getWindow().getScaledWidth() / scale;
        matrices.pushMatrix();
        matrices.scale(scale, scale, 1.0f);
        try {
            float offsetY = MARGIN;
            List<Long> finished = new ArrayList<>();
            for (Map.Entry<Long, Card> entry : CARDS.entrySet()) {
                Card card = entry.getValue();
                float step = Animation.frameDelta() / (card.leaving ? HIDE_SECONDS : SPAWN_SECONDS);
                card.phase = MathHelper.clamp(card.phase + (card.leaving ? -step : step), 0.0f, 1.0f);
                if (card.leaving && card.phase <= 0.0f) {
                    finished.add(entry.getKey());
                    continue;
                }
                float height = renderOne(matrices, card, right, offsetY);
                offsetY += (height + GAP) * easeOut(card.phase);
            }
            finished.forEach(CARDS::remove);
        } finally {
            matrices.popMatrix();
        }
    }

    private static void sync(List<Notification> notifications) {
        CARDS.values().forEach(card -> card.present = false);
        for (Notification notification : notifications) {
            Card card = CARDS.computeIfAbsent(notification.getId(), id -> new Card());
            card.present = true;
            card.type = notification.getType();
            card.title = notification.getTitle();
            card.text = notification.getText();
            card.persistent = notification.isPersistent();
            card.lifeTicks = notification.getLifeTicks();
            card.leaving = notification.getState() == Notification.State.HIDING;
            if (notification.getState() == Notification.State.IDLE) {
                updateLife(card, notification.getElapsedTicks());
            }
        }
        CARDS.values().stream().filter(card -> !card.present).forEach(card -> card.leaving = true);
    }

    private static void updateLife(Card card, int elapsedTicks) {
        if (card.lifeTicks <= 0 || card.persistent) {
            card.lifeLeft = 1.0f;
            return;
        }
        if (elapsedTicks != card.lastElapsedTicks) {
            card.lastElapsedTicks = elapsedTicks;
            card.subTick = 0.0f;
        } else {
            card.subTick = Math.min(1.0f, card.subTick + Animation.frameDelta() * TICKS_PER_SECOND);
        }
        card.lifeLeft = 1.0f - MathHelper.clamp((elapsedTicks + card.subTick) / card.lifeTicks, 0.0f, 1.0f);
    }

    private static float easeOut(float t) {
        float inverted = 1.0f - t;
        return 1.0f - inverted * inverted * inverted;
    }

    private static float slideOffset(Card card) {
        if (!card.leaving) {
            return (1.0f - easeOut(card.phase)) * SLIDE;
        }
        float travel = MathHelper.clamp((1.0f - card.phase) / TRAVEL_WINDOW, 0.0f, 1.0f);
        return travel * travel * SLIDE;
    }

    private static float alphaOf(Card card) {
        if (!card.leaving) {
            return easeOut(card.phase);
        }
        return MathHelper.clamp(card.phase / FADE_TAIL, 0.0f, 1.0f);
    }

    private static float renderOne(Matrix4fStack matrices, Card card, float right, float targetY) {
        float textLeft = PADDING + ICON + ICON_GAP;
        float textWidth = BASE_WIDTH - textLeft - PADDING;
        List<Run> titleRuns = TITLE.ellipsize(FormattedText.parse(card.title, TITLE_COLOR), textWidth);
        List<Run> bodyRuns = FormattedText.parse(card.text, TEXT_COLOR);
        List<List<Run>> lines = new ArrayList<>();
        for (List<Run> line : BODY.wrap(bodyRuns, textWidth)) {
            lines.add(BODY.ellipsize(line, textWidth));
        }
        float titleHeight = TITLE.getLineHeight();
        float bodyHeight = lines.isEmpty() ? 0.0f : lines.size() * (BODY.getLineHeight() + LINE_GAP) - LINE_GAP;
        float content = titleHeight + (lines.isEmpty() ? 0.0f : 3.0f + bodyHeight);
        float height = PADDING * 2.0f + Math.max(ICON, content);
        if (!card.placed) {
            card.y = targetY;
            card.placed = true;
        } else {
            card.y = (float) Animation.fast(card.y, targetY, 18.0);
        }
        float x = right - MARGIN - BASE_WIDTH + slideOffset(card);
        float y = card.y;
        BufferRenderer.pushAlpha(alphaOf(card));
        try {
            RECT.setRadius(new QuadRadiusState(RADIUS));
            RECT.setSize(new SizeState(BASE_WIDTH, height));
            RECT.setColor(new QuadColorState(CARD_EDGE));
            RECT.render(matrices, x, y);
            RECT.setSize(new SizeState(BASE_WIDTH - 2.0f, height - 2.0f));
            RECT.setColor(new QuadColorState(CARD));
            RECT.render(matrices, x + 1.0f, y + 1.0f);
            renderIcon(matrices, card.type, x + PADDING, y + (height - ICON) / 2.0f);
            float textY = y + PADDING + Math.max(0.0f, (Math.max(ICON, content) - content) / 2.0f);
            TITLE.render(matrices, titleRuns, x + textLeft, textY);
            textY += titleHeight + 3.0f;
            for (List<Run> line : lines) {
                BODY.render(matrices, line, x + textLeft, textY);
                textY += BODY.getLineHeight() + LINE_GAP;
            }
            if (!card.persistent && card.lifeTicks > 0) {
                renderProgress(matrices, card, x, y, height);
            }
        } finally {
            BufferRenderer.popAlpha();
        }
        return height;
    }

    private static void renderIcon(Matrix4fStack matrices, NotificationType type, float x, float y) {
        RECT.setRadius(new QuadRadiusState(ICON_RADIUS));
        RECT.setSize(new SizeState(ICON, ICON));
        RECT.setColor(new QuadColorState(type.getOutline()));
        RECT.render(matrices, x, y);
        float cx = x + ICON / 2.0f;
        float cy = y + ICON / 2.0f;
        switch (type) {
            case SUCCESS -> {
                segment(matrices, cx - 4.6f, cy + 0.2f, cx - 1.4f, cy + 3.6f);
                segment(matrices, cx - 1.4f, cy + 3.6f, cx + 4.8f, cy - 3.6f);
            }
            case ERROR -> {
                segment(matrices, cx - 3.6f, cy - 3.6f, cx + 3.6f, cy + 3.6f);
                segment(matrices, cx - 3.6f, cy + 3.6f, cx + 3.6f, cy - 3.6f);
            }
            default -> {
                segment(matrices, cx, cy - 5.0f, cx, cy + 1.2f);
                dot(matrices, cx, cy + 5.0f);
            }
        }
    }

    private static void segment(Matrix4fStack matrices, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy) + GLYPH_THICKNESS;
        float degrees = (float) Math.toDegrees(Math.atan2(dy, dx));
        BAR.setSize(new SizeState(length, GLYPH_THICKNESS));
        BAR.setRadius(new QuadRadiusState(GLYPH_THICKNESS / 2.0f));
        BAR.setColor(new QuadColorState(GLYPH));
        matrices.pushMatrix();
        matrices.translate((x1 + x2) / 2.0f, (y1 + y2) / 2.0f, 0.0f);
        matrices.rotateZ((float) Math.toRadians(degrees));
        BAR.render(matrices, -length / 2.0f, -GLYPH_THICKNESS / 2.0f);
        matrices.popMatrix();
    }

    private static void dot(Matrix4fStack matrices, float x, float y) {
        BAR.setSize(new SizeState(DOT_SIZE, DOT_SIZE));
        BAR.setRadius(new QuadRadiusState(DOT_SIZE / 2.5f));
        BAR.setColor(new QuadColorState(GLYPH));
        BAR.render(matrices, x - DOT_SIZE / 2.0f, y - DOT_SIZE / 2.0f);
    }

    private static void renderProgress(Matrix4fStack matrices, Card card, float x, float y, float height) {
        float trackWidth = BASE_WIDTH - RADIUS * 2.0f;
        float barY = y + height - PROGRESS_HEIGHT - 3.0f;
        Color accent = card.type.getOutline();
        BAR.setRadius(new QuadRadiusState(PROGRESS_HEIGHT / 2.0f));
        BAR.setSize(new SizeState(trackWidth, PROGRESS_HEIGHT));
        BAR.setColor(new QuadColorState(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 45)));
        BAR.render(matrices, x + RADIUS, barY);
        if (card.lifeLeft > 0.005f) {
            BAR.setSize(new SizeState(trackWidth * card.lifeLeft, PROGRESS_HEIGHT));
            BAR.setColor(new QuadColorState(accent));
            BAR.render(matrices, x + RADIUS, barY);
        }
    }
}
