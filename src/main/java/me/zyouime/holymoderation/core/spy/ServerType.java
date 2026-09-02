package me.zyouime.holymoderation.core.spy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerType {

    CLASSIC("anarchy", "classic", "anarchy"),
    LITE("lanarchy", "lite", "lite-anarchy"),
    LITE120("l2anarchy", "lite120", "lite120-anarchy"),
    LPVP("lpvp", "lpvp", "lpvp"),
    LOBBY("lobby", "lobby", "lobby");

    private final String rawPrefix;
    private final String label;
    private final String displayPrefix;
}

