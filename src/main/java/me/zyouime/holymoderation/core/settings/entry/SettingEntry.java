package me.zyouime.holymoderation.core.settings.entry;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.Setting;

public sealed interface SettingEntry<T> permits ToggleEntry, TextEntry, NumberEntry {

    String key();
    String title();
    Setting<T> setting();
    Optional<String> validate(T value);
    String display();
    default T value() {
        return setting().getValue();
    }
    default T defaultValue() {
        return setting().getDefaultValue();
    }
}
