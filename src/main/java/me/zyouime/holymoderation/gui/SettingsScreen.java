package me.zyouime.holymoderation.gui;

import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.settings.SettingsCatalog;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.panel.CategoryListWidget;
import me.zyouime.holymoderation.gui.panel.DefaultCategory;
import me.zyouime.holymoderation.gui.panel.setting.BooleanSetting;
import me.zyouime.holymoderation.gui.panel.setting.SliderSetting;
import me.zyouime.holymoderation.gui.panel.setting.TextListSetting;
import me.zyouime.holymoderation.gui.panel.setting.TextSetting;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.render.animation.impl.EaseOut;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.msdf.Icons;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.render.utils.BufferRenderer;
import me.zyouime.holymoderation.resources.Fonts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;

import java.awt.*;

public class SettingsScreen extends AbstractScreen {

    private static final float MARGIN = 24.0f;
    private static final float MIN_WIDTH = 240.0f;
    private static final float MAX_WIDTH = 400.0f;
    private static final float MIN_HEIGHT = 150.0f;
    private static final float MAX_HEIGHT = 330.0f;
    private static final float HEADER_HEIGHT = 44.0f;
    private static final float CARD_PADDING = 16.0f;
    private static final float LIST_TOP_GAP = 10.0f;
    private static final float LIST_BOTTOM_GAP = 12.0f;
    private static final float COLUMN_GAP = 6.0f;
    private static final float MIN_COLUMN_WIDTH = 150.0f;
    private static final float RADIUS = 9.0f;
    private static final float ICON_BACK_SIZE = 24f;
    private final ModSettings settings;
    private final EaseOut appear = new EaseOut(15f);
    private CategoryListWidget categories;
    private BuiltRectangle backdrop;
    private BuiltRectangle card;
    private BuiltRectangle header;
    private BuiltRectangle divider;
    private BuiltRectangle iconBack;
    private BuiltText title;
    private BuiltText subtitle;
    private BuiltText hint;
    private BuiltText icon;

    public SettingsScreen(Screen parent, ModSettings settings) {
        super(parent);
        this.settings = settings;
    }

    private static Window window() {
        return MinecraftProvider.window();
    }

    private static float cardWidth() {
        return MathHelper.clamp(window().getScaledWidth() - MARGIN * 2.0f, MIN_WIDTH, MAX_WIDTH);
    }

    private static float cardHeight() {
        return MathHelper.clamp(window().getScaledHeight() - MARGIN * 2.0f, MIN_HEIGHT, MAX_HEIGHT);
    }

    private static float cardX() {
        return (window().getScaledWidth() - cardWidth()) / 2.0f;
    }

    private static float cardY() {
        return (window().getScaledHeight() - cardHeight()) / 2.0f;
    }

    private static float listWidth() {
        return cardWidth() - CARD_PADDING * 2.0f;
    }
    private static float columnWidth() {
        float column = (listWidth() - COLUMN_GAP) / 2.0f;
        return column < MIN_COLUMN_WIDTH ? listWidth() : column;
    }

    @Override
    protected void init() {
        ModSounds.playSound(ModSounds.GUI_OPEN);
        this.widgets.clear();
        this.buildRenderers();
        this.categories = this.addWidget(new CategoryListWidget(0.0f, 0.0f, listWidth(), 0.0f, COLUMN_GAP));
        this.buildCategories();
        this.updateLayout();
    }

    private void buildRenderers() {
        this.backdrop = Builder.rectangle()
                .size(SizeState.NONE)
                .radius(QuadRadiusState.NO_ROUND)
                .color(new QuadColorState(GuiColors.BACKDROP))
                .build();
        this.card = Builder.rectangle()
                .size(new SizeState(cardWidth(), cardHeight()))
                .radius(new QuadRadiusState(RADIUS))
                .color(new QuadColorState(GuiColors.CARD))
                .build();
        this.header = Builder.rectangle()
                .size(new SizeState(cardWidth(), HEADER_HEIGHT))
                .radius(new QuadRadiusState(RADIUS, 0.0f, 0.0f, RADIUS))
                .color(new QuadColorState(GuiColors.CARD_HEADER))
                .build();
        this.divider = Builder.rectangle()
                .size(new SizeState(cardWidth(), 1.0f))
                .radius(QuadRadiusState.NO_ROUND)
                .color(new QuadColorState(GuiColors.DIVIDER))
                .build();
        this.title = Builder.text()
                .text("HolyModeration")
                .size(14.0f)
                .color(GuiColors.TEXT)
                .thickness(0.07f)
                .smoothness(0.5f)
                .font(Fonts.UI.get())
                .build();
        this.subtitle = Builder.text()
                .text("Настройки мода")
                .size(8.0f)
                .color(GuiColors.TEXT_MUTED)
                .thickness(0.05f)
                .font(Fonts.UI.get())
                .build();
        this.hint = Builder.text()
                .text("Правый клик по настройке сбрасывает её")
                .size(7.0f)
                .color(GuiColors.TEXT_MUTED)
                .thickness(0.05f)
                .font(Fonts.UI.get())
                .build();
        this.iconBack = Builder.rectangle()
                .size(new SizeState(ICON_BACK_SIZE, ICON_BACK_SIZE))
                .radius(new QuadRadiusState(4))
                .color(new QuadColorState(GuiColors.ACCENT))
                .build();
        this.icon = Builder.text()
                .size(8f)
                .font(Fonts.ICONS.get())
                .color(-1)
                .build();
    }

    private void buildCategories() {
        float column = columnWidth();
        float wide = listWidth();
        DefaultCategory auto = new DefaultCategory("Автодействия", column, Icons.BOLT);
        auto.addSetting(new BooleanSetting(this.settings.autoVanish, "Автованиш"));
        auto.addSetting(new BooleanSetting(this.settings.autoFly, "Автофлай"));
        auto.addSetting(new BooleanSetting(this.settings.autoGm3, "Автогм3"));
        auto.addSetting(new BooleanSetting(this.settings.autoGod, "Автогод"));
        auto.addSetting(new BooleanSetting(this.settings.autoHacAlerts, "HAC alerts"));
        DefaultCategory checkout = new DefaultCategory("Проверка", column, Icons.SEARCH);
        checkout.addSetting(new BooleanSetting(this.settings.autoCheckoutTp, "Телепорт на /warp logo"));
        checkout.addSetting(new BooleanSetting(this.settings.autoAnyDesk, "Копировать ID AnyDesk"));
        checkout.addSetting(new BooleanSetting(this.settings.dupeIp, "Автоматический /dupeip"));
        DefaultCategory spy = new DefaultCategory("Слежка", column, Icons.EYE);
        spy.addSetting(new BooleanSetting(this.settings.autoSpyTp, "Телепорт при слежке"));
        spy.addSetting(new SliderSetting(this.settings.spyDelay, "Задержка обновления", SettingsCatalog.SPY_DELAY_MIN, SettingsCatalog.SPY_DELAY_MAX));
        DefaultCategory chat = new DefaultCategory("Чат", column, Icons.CHAT);
        chat.addSetting(new BooleanSetting(this.settings.copyButton, "Кнопка копирования"));
        chat.addSetting(new TextSetting(this.settings.copyButtonText, "Текст кнопки")
                .colorCodes(true)
                .maxLength(SettingsCatalog.MAX_TEXT_LENGTH)
                .placeholder("&f&l[&a&lcopy&f&l]"));
        chat.addSetting(new TextSetting(this.settings.playerMarker, "Метка игрока на проверке")
                .colorCodes(true)
                .maxLength(SettingsCatalog.MAX_TEXT_LENGTH)
                .placeholder("&d&l[CHECK]"));
        DefaultCategory indicatorHud = new DefaultCategory("Индикаторы", column, Icons.PIN);
        indicatorHud.addSetting(new BooleanSetting(this.settings.spyHudEnabled, "Показывать слежку"));
        indicatorHud.addSetting(new SliderSetting(this.settings.spyHudScale, "Масштаб, %", SettingsCatalog.HUD_SCALE_MIN, SettingsCatalog.HUD_SCALE_MAX));
        indicatorHud.addSetting(new BooleanSetting(this.settings.checkoutHudEnabled, "Показывать данные о проверке"));
        indicatorHud.addSetting(new SliderSetting(this.settings.checkoutHudScale, "Масштаб, %", SettingsCatalog.HUD_SCALE_MIN, SettingsCatalog.HUD_SCALE_MAX));
        DefaultCategory obsCategory = new DefaultCategory("OBS", column);
        obsCategory.addSetting(new BooleanSetting(this.settings.obsEnabled, "Включить авто-запись"));
        obsCategory.addSetting(new TextSetting(this.settings.obsHost, "Хост (IP)"));
        obsCategory.addSetting(new TextSetting(this.settings.obsPort, "Порт (Число)"));
        obsCategory.addSetting(new TextSetting(this.settings.obsPassword, "Пароль (Если есть авторизация)"));
        DefaultCategory journal = new DefaultCategory("Журнал", wide, Icons.FILE);
        journal.addSetting(new TextSetting(wide, 38.0f, this.settings.apiToken, "Токен журнала")
                .maxLength(SettingsCatalog.MAX_TEXT_LENGTH)
                .placeholder("не задан"));
        journal.addSetting(new TextSetting(wide, 38.0f, this.settings.vkLink, "Ссылка на ВК")
                .maxLength(SettingsCatalog.MAX_TEXT_LENGTH)
                .placeholder("https://vk.com/"));
        DefaultCategory notifications = new DefaultCategory("Уведомления", column, Icons.DESKTOP);
        notifications.addSetting(new SliderSetting(this.settings.notificationScale, "Масштаб, %", SettingsCatalog.NOTIFICATION_SCALE_MIN, SettingsCatalog.NOTIFICATION_SCALE_MAX));
        DefaultCategory texts = new DefaultCategory("Тексты для проверки", wide, Icons.TEXT);
        texts.addSetting(new TextListSetting(wide, this.settings.checkoutTexts, "Сообщения, отправляемые по /hm sendtexts", SettingsCatalog.MAX_TEXT_LENGTH));
        this.categories.addCategory(auto);
        this.categories.addCategory(checkout);
        this.categories.addCategory(spy);
        this.categories.addCategory(chat);
        this.categories.addCategory(indicatorHud);
        this.categories.addCategory(obsCategory);
        this.categories.addCategory(notifications);
        this.categories.addCategory(journal);
        this.categories.addCategory(texts);
    }

    private void updateLayout() {
        float width = cardWidth();
        float height = cardHeight();
        float x = cardX();
        float y = cardY();
        this.card.setSize(new SizeState(width, height));
        this.header.setSize(new SizeState(width, HEADER_HEIGHT));
        this.divider.setSize(new SizeState(width, 1.0f));
        this.categories.updatePos(x + CARD_PADDING, y + HEADER_HEIGHT + LIST_TOP_GAP, listWidth(), height - HEADER_HEIGHT - LIST_TOP_GAP - LIST_BOTTOM_GAP);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.updateLayout();
    }

    @Override
    public void customRender(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        if (this.categories == null) {
            return;
        }
        this.appear.update();
        float progress = this.appear.getAnimation();
        this.backdrop.setSize(new SizeState(window().getScaledWidth(), window().getScaledHeight()));
        this.backdrop.render(matrices, 0.0f, 0.0f);
        float x = cardX();
        float y = cardY();
        float width = cardWidth();
        float centerX = x + width / 2.0f;
        float centerY = y + cardHeight() / 2.0f;
        float scale = 0.98f + 0.02f * progress;
        BufferRenderer.pushAlpha(progress);
        matrices.pushMatrix();
        matrices.translate(centerX, centerY, 0.0f);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate(-centerX, -centerY, 0.0f);
        try {
            this.card.render(matrices, x, y);
            this.header.render(matrices, x, y);
            this.divider.render(matrices, x, y + HEADER_HEIGHT - 1.0f);
            float iconBackX = x + CARD_PADDING;
            float titleX = iconBackX + ICON_BACK_SIZE + CARD_PADDING / 2;
            this.title.render(matrices, titleX, y + 7.0f);
            this.subtitle.render(matrices, titleX, y + 23.0f);
            this.iconBack.render(matrices, iconBackX, y + 9f);
            this.icon.setSize(14f);
            this.icon.setText(Icons.SHIELD_CHECK.unicode);
            this.icon.setColor(Color.BLACK);
            this.icon.render(matrices, iconBackX + ICON_BACK_SIZE / 2f - this.icon.getTextWidth() / 2f, y + 13.5f);
            this.icon.setText(Icons.REFRESH.unicode);
            this.icon.setColor(GuiColors.ACCENT);
            this.icon.setSize(8f);
            float hintX = x + width - this.hint.getTextWidth() - CARD_PADDING;
            float hintY = y + HEADER_HEIGHT - this.hint.getLineHeight() - 9.0f;
            this.icon.render(matrices, hintX - 2 - icon.getSize(), hintY);
            this.hint.render(matrices, hintX, hintY);
            this.categories.render(matrices, mouseX, mouseY, delta);
        } finally {
            matrices.popMatrix();
            BufferRenderer.popAlpha();
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (this.settings != null) {
            this.settings.saveSettings();
        }
    }
}
