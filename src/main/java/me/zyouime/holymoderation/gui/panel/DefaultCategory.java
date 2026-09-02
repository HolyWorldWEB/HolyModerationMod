package me.zyouime.holymoderation.gui.panel;

import lombok.Getter;
import me.zyouime.holymoderation.gui.GuiColors;
import me.zyouime.holymoderation.gui.panel.setting.PanelSetting;
import me.zyouime.holymoderation.gui.widget.api.Elements;
import me.zyouime.holymoderation.render.builders.Builder;
import me.zyouime.holymoderation.render.builders.states.QuadColorState;
import me.zyouime.holymoderation.render.builders.states.QuadRadiusState;
import me.zyouime.holymoderation.render.builders.states.SizeState;
import me.zyouime.holymoderation.render.msdf.Icons;
import me.zyouime.holymoderation.render.renderers.impl.BuiltRectangle;
import me.zyouime.holymoderation.render.renderers.impl.BuiltText;
import me.zyouime.holymoderation.resources.Fonts;
import org.joml.Matrix4fStack;

public class DefaultCategory extends AbstractCategory {

    public static final float HEADER_HEIGHT = 24.0f;
    private static final float RADIUS = 6.0f;
    private static final float SETTING_SPACING = 1.0f;
    private static final float BOTTOM_PADDING = 8.0f;
    @Getter
    private final String title;
    private BuiltText titleRender;
    private BuiltRectangle background;
    private BuiltRectangle header;
    private BuiltRectangle divider;
    private BuiltRectangle accent;
    private final Icons icon;
    private BuiltText iconRender;

    public DefaultCategory(String title) {
        this(title, PanelSetting.DEFAULT_WIDTH, null);
    }

    public DefaultCategory(String title, float width) {
        this(title, width, null);
    }

    public DefaultCategory(String title, float width, Icons icon) {
        super(width);
        this.title = title;
        this.icon = icon;
    }

    @Override
    public void init() {
        super.init();
        this.titleRender = Builder.text()
                .size(9.5f)
                .text(this.title)
                .color(GuiColors.TEXT)
                .thickness(0.06f)
                .smoothness(0.5f)
                .font(Fonts.UI.get())
                .build();
        this.background = Builder.rectangle()
                .size(new SizeState(this.width, 0.0f))
                .color(new QuadColorState(GuiColors.CATEGORY))
                .radius(new QuadRadiusState(RADIUS))
                .build();
        this.header = Builder.rectangle()
                .size(new SizeState(this.width, HEADER_HEIGHT))
                .color(new QuadColorState(GuiColors.CATEGORY_HEADER))
                .radius(new QuadRadiusState(RADIUS, 0.0f, 0.0f, RADIUS))
                .build();
        if (icon == null) {
            this.accent = Builder.rectangle()
                    .size(new SizeState(2.0f, 10.0f))
                    .color(new QuadColorState(GuiColors.ACCENT))
                    .radius(new QuadRadiusState(1.0f))
                    .build();
        } else {
            this.iconRender = Builder.text()
                    .size(9f)
                    .color(GuiColors.ACCENT)
                    .font(Fonts.ICONS.get())
                    .build();
        }
        this.divider = Builder.rectangle()
                .size(new SizeState(this.width, 1.0f))
                .color(new QuadColorState(GuiColors.DIVIDER))
                .radius(QuadRadiusState.NO_ROUND)
                .build();
    }

    @Override
    protected void renderElement(Matrix4fStack matrices, double mouseX, double mouseY, float delta) {
        this.background.setSize(new SizeState(this.width, this.height));
        this.header.setSize(new SizeState(this.width, HEADER_HEIGHT));
        this.divider.setSize(new SizeState(this.width, 1.0f));
        this.background.render(matrices, this.x, this.y);
        this.header.render(matrices, this.x, this.y);
        this.divider.render(matrices, this.x, this.y + HEADER_HEIGHT - 1.0f);
        float x = this.x + 10f;
        float y = this.y + (HEADER_HEIGHT - 10.0f) / 2.0f;
        if (icon == null) {
            this.accent.render(matrices, x, y);
        } else {
            iconRender.setText(icon.unicode);
            iconRender.render(matrices, x - 4f, y + 1);
        }
        this.titleRender.setMaxWidth(this.width - 32.0f);
        this.titleRender.render(matrices, this.x + 18.0f, this.y + (HEADER_HEIGHT - this.titleRender.getLineHeight()) / 2.0f);
        for (PanelSetting<?> setting : Elements.expandedLast(this.settings)) {
            setting.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public void updatePos(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        float settingY = y + HEADER_HEIGHT + 4.0f;
        for (PanelSetting<?> setting : this.settings) {
            setting.updatePos(x, settingY, width, setting.getHeight());
            settingY += setting.getHeight() + SETTING_SPACING;
        }
        this.height = (settingY - y) + BOTTOM_PADDING - (this.settings.isEmpty() ? 0.0f : SETTING_SPACING);
    }
}
