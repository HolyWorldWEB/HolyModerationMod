package me.zyouime.holymoderation.core.settings.entry;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.Setting;

public record NumberEntry(String key, String title, Setting<Integer> setting, int min, int max) implements SettingEntry<Integer> {

    @Override
    public Optional<String> validate(Integer value) {
        if (value < min || value > max) {
            return Optional.of("Значение должно быть от %d до %d.".formatted(min, max));
        }
        return Optional.empty();
    }

    @Override
    public String display() {
        return String.valueOf(value());
    }
}
