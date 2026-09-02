package me.zyouime.holymoderation.core.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Agreement {

    MASCULINE("включён", "выключен"),
    FEMININE("включена", "выключена"),
    NEUTER("включено", "выключено"),
    PLURAL("включены", "выключены");

    private final String enabled;
    private final String disabled;

    public String describe(boolean value) {
        return value ? enabled : disabled;
    }
}
