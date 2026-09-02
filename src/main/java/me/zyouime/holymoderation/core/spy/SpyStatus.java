package me.zyouime.holymoderation.core.spy;

import java.util.Optional;

public record SpyStatus(Type type, ServerLocation location) {

    private static final SpyStatus UNKNOWN = new SpyStatus(Type.UNKNOWN, null);
    private static final SpyStatus OFFLINE = new SpyStatus(Type.OFFLINE, null);
    private static final SpyStatus PAUSED = new SpyStatus(Type.PAUSED, null);

    public static SpyStatus unknown() {
        return UNKNOWN;
    }

    public static SpyStatus offline() {
        return OFFLINE;
    }

    public static SpyStatus paused() {
        return PAUSED;
    }

    public static SpyStatus online(ServerLocation location) {
        return new SpyStatus(Type.ONLINE, location);
    }

    public boolean isOnline() {
        return type == Type.ONLINE;
    }

    public boolean isKnown() {
        return type != Type.UNKNOWN;
    }

    public Optional<ServerLocation> serverLocation() {
        return Optional.ofNullable(location);
    }

    public boolean isOnSameServerAs(ServerLocation userLocation) {
        if (type != Type.ONLINE) {
            return false;
        }
        return location.equals(userLocation);
    }

    public String display() {
        return switch (type) {
            case ONLINE -> location.display();
            case OFFLINE -> "не в сети";
            case PAUSED -> "на паузе";
            case UNKNOWN -> "";
        };
    }


    public enum Type {
        UNKNOWN,
        ONLINE,
        OFFLINE,
        PAUSED
    }
}
