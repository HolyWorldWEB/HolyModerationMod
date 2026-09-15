package me.zyouime.holymoderation.core.settings;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.settings.entry.SettingEntry;

@RequiredArgsConstructor
public final class SettingsEditor {

    private final ModSettings settings;
    private boolean dirty = false;

    public <T> Optional<String> apply(SettingEntry<T> entry, T value) {
        Optional<String> problem = entry.validate(value);
        if (problem.isPresent()) {
            return problem;
        }
        entry.setting().setValue(value);
        dirty = true;
        return Optional.empty();
    }

    public void reset(SettingEntry<?> entry) {
        entry.setting().reset();
        dirty = true;
    }

    public void flush() {
        if (!dirty) {
            return;
        }
        settings.saveSettings();
        dirty = false;
    }
}
