package me.zyouime.holymoderation.core.spy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerType {

    CLASSIC("anarchy", "classic", "anarchy"),
    LITE("lanarchy", "lite", "lite-anarchy"),
    LITE121("l21anarchy", "lite121", "lite121-anarchy"),
    LPVP("lpvp", "lpvp", "lpvp"),
    PRIME("prime", "prime", "prime"),
    LOBBY("lobby", "lobby", "lobby");

    private final String rawPrefix;
    private final String label;
    private final String displayPrefix;
}

