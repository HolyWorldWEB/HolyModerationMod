package me.zyouime.holymoderation.core.spy;

import java.util.Optional;

public record ServerLocation(ServerType type, int number) {

    private static final int NO_NUMBER = 0;
    public static final int DEFAULT_NUMBER = 1;

    public static ServerLocation of(ServerType type) {
        return new ServerLocation(type, NO_NUMBER);
    }

    public static Optional<ServerLocation> parseDisplay(String display) {
        if (display == null || display.isBlank()) {
            return Optional.empty();
        }
        String normalized = display.trim().toLowerCase();
        return matchDisplay(normalized, ServerType.LITE120)
                .or(() -> matchDisplay(normalized, ServerType.LITE))
                .or(() -> matchDisplay(normalized, ServerType.CLASSIC))
                .or(() -> matchDisplay(normalized, ServerType.LPVP))
                .or(() -> matchDisplay(normalized, ServerType.LOBBY));
    }

    private static Optional<ServerLocation> matchDisplay(String normalized, ServerType type) {
        if (!normalized.startsWith(type.getDisplayPrefix())) {
            return Optional.empty();
        }
        String tail = normalized.substring(type.getDisplayPrefix().length());
        if (tail.isEmpty()) {
            return Optional.of(of(type, DEFAULT_NUMBER));
        }
        if (!tail.startsWith("-")) {
            return Optional.empty();
        }
        try {
            int parsed = Integer.parseInt(tail.substring(1));
            if (parsed < 1) {
                return Optional.empty();
            }
            return Optional.of(of(type, parsed));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static ServerLocation of(ServerType type, int number) {
        return new ServerLocation(type, number);
    }

    public static Optional<ServerLocation> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        String normalized = raw.trim().toLowerCase();
        if (normalized.startsWith(ServerType.LOBBY.getRawPrefix())) {
            return Optional.of(of(ServerType.LOBBY));
        }
        if (normalized.startsWith(ServerType.LPVP.getRawPrefix())) {
            return Optional.of(of(ServerType.LPVP));
        }
        return parseNumbered(normalized, ServerType.LITE120).or(() -> parseNumbered(normalized, ServerType.LITE)).or(() -> parseNumbered(normalized, ServerType.CLASSIC));
    }

    private static Optional<ServerLocation> parseNumbered(String normalized, ServerType type) {
        if (!normalized.startsWith(type.getRawPrefix())) {
            return Optional.empty();
        }
        String tail = normalized.substring(type.getRawPrefix().length());
        if (tail.isEmpty()) {
            return Optional.of(of(type, DEFAULT_NUMBER));
        }
        try {
            int parsed = Integer.parseInt(tail);
            if (parsed < 1) {
                return Optional.empty();
            }
            return Optional.of(of(type, parsed));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public boolean isLobby() {
        return type == ServerType.LOBBY;
    }

    public String display() {
        if (number > NO_NUMBER) {
            return "%s-%d".formatted(type.getLabel(), number);
        }
        return type.getLabel();
    }

    @Override
    public String toString() {
        return display();
    }
}
