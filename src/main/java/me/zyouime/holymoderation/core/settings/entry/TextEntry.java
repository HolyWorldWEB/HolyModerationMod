package me.zyouime.holymoderation.core.settings.entry;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.Setting;

public record TextEntry(String key, String title, Setting<String> setting, int maxLength, boolean masked) implements SettingEntry<String> {

    private static final String MASK = "•••";
    private static final String EMPTY_DISPLAY = "не задано";

    @Override
    public Optional<String> validate(String value) {
        if (value.length() > maxLength) {
            return Optional.of("Слишком длинный текст: %d символов, максимум %d.".formatted(value.length(), maxLength));
        }
        return Optional.empty();
    }

    @Override
    public String display() {
        String value = value();
        if (value.isEmpty()) {
            return EMPTY_DISPLAY;
        }
        return masked ? MASK : value;
    }
}
