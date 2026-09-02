package me.zyouime.holymoderation.core.punishment;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PunishmentType {

    MUTE("mute", Kind.MUTE, TimeMode.OPTIONAL, true),
    MUTEIP("muteip", Kind.MUTE, TimeMode.OPTIONAL, true),
    TEMPMUTE("tempmute", Kind.MUTE, TimeMode.REQUIRED, false),
    TEMPMUTEIP("tempmuteip", Kind.MUTE, TimeMode.REQUIRED, false),
    BAN("ban", Kind.BAN, TimeMode.OPTIONAL, true),
    BANIP("banip", Kind.BAN, TimeMode.OPTIONAL, true),
    TEMPBAN("tempban", Kind.BAN, TimeMode.REQUIRED, true),
    WARN("warn", Kind.WARN, TimeMode.NONE, false);

    private final String command;
    private final Kind kind;
    private final TimeMode timeMode;
    private final boolean supportsVk;

    public static Optional<PunishmentType> byCommand(String command) {
        return Arrays.stream(values()).filter(type -> type.command.equalsIgnoreCase(command)).findFirst();
    }

    public boolean isBan() {
        return kind == Kind.BAN;
    }

    public boolean isMute() {
        return kind == Kind.MUTE;
    }

    public boolean isWarn() {
        return kind == Kind.WARN;
    }

    public boolean endsCheckout() {
        return kind == Kind.BAN || kind == Kind.WARN;
    }

    public boolean acceptsTime() {
        return timeMode != TimeMode.NONE;
    }

    public boolean requiresTime() {
        return timeMode == TimeMode.REQUIRED;
    }

    public enum TimeMode {
        NONE,
        OPTIONAL,
        REQUIRED
    }

    public enum Kind {
        MUTE,
        BAN,
        WARN
    }
}
