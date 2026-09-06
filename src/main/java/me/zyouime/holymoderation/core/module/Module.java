package me.zyouime.holymoderation.core.module;

import java.util.List;

import me.zyouime.holymoderation.core.command.ModCommand;

import lombok.Getter;

public abstract class Module {

    @Getter
    private boolean enabled = true;

    public String name() {
        return getClass().getSimpleName().replace("Module", "").toLowerCase();
    }

    public abstract void init();

    public boolean canDisable() {
        return true;
    }

    public List<ModCommand> commands() {
        return List.of();
    }

    public void tick() {}

    public final void setEnabled(boolean enabled) {
        if (!enabled && !canDisable()) {
            return;
        }
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }
}
