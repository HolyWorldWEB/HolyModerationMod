package me.zyouime.holymoderation.core.settings.entry;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.core.settings.Agreement;

public record ToggleEntry(String key, String title, Agreement agreement, Setting<Boolean> setting) implements SettingEntry<Boolean> {

    @Override
    public Optional<String> validate(Boolean value) {
        return Optional.empty();
    }

    @Override
    public String display() {
        return Agreement.NEUTER.describe(value());
    }

    public boolean toggled() {
        return !value();
    }
}
