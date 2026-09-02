package me.zyouime.holymoderation.gui.panel;

import lombok.Getter;
import me.zyouime.holymoderation.gui.panel.setting.PanelSetting;
import me.zyouime.holymoderation.gui.widget.api.AbstractElement;
import me.zyouime.holymoderation.gui.widget.api.Animated;
import me.zyouime.holymoderation.gui.widget.api.Elements;
import me.zyouime.holymoderation.gui.widget.api.Expandable;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractCategory extends AbstractElement implements Animated, Expandable {

    protected final List<PanelSetting<?>> settings = new ArrayList<>();

    public AbstractCategory(float width) {
        super(0.0f, 0.0f, width, 0.0f);
    }

    public void init() {
        for (PanelSetting<?> setting : this.settings) {
            setting.init();
        }
    }

    public <T extends PanelSetting<?>> T addSetting(T setting) {
        this.settings.add(setting);
        return setting;
    }

    @Override
    public boolean isExpanded() {
        for (PanelSetting<?> setting : this.settings) {
            if (setting.isExpanded()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void collapse() {
        Elements.collapseAll(this.settings);
    }

    @Override
    public void resetAnim() {
        for (PanelSetting<?> setting : this.settings) {
            setting.resetAnim();
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (PanelSetting<?> setting : this.settings) {
            setting.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int modifiers) {
        for (PanelSetting<?> setting : this.settings) {
            if (setting.keyPressed(keyCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int modifiers) {
        for (PanelSetting<?> setting : this.settings) {
            if (setting.keyReleased(keyCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (PanelSetting<?> setting : this.settings) {
            if (setting.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (PanelSetting<?> setting : Elements.expandedFirst(this.settings)) {
            if (setting.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (PanelSetting<?> setting : this.settings) {
            if (setting.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (PanelSetting<?> setting : this.settings) {
            if (setting.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (PanelSetting<?> setting : Elements.expandedFirst(this.settings)) {
            if (setting.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }
}
