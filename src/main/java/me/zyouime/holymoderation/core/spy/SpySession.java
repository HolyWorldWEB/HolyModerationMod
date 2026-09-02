package me.zyouime.holymoderation.core.spy;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

@Getter
public final class SpySession {

    private final String player;
    private SpyStatus status = SpyStatus.unknown();
    private String activity = StringUtils.EMPTY;
    private ServerLocation lastKnownLocation = null;
    private boolean processingPlaytimeInfo = false;
    private boolean awaitingResponse = false;
    private int ticksSinceRequest = 0;

    public SpySession(String player) {
        this.player = player;
    }

    public void status(SpyStatus status) {
        this.status = status;
    }

    public void activity(String activity) {
        if (activity == null) {
            this.activity = StringUtils.EMPTY;
            return;
        }
        this.activity = activity;
    }

    public void lastKnownLocation(ServerLocation location) {
        this.lastKnownLocation = location;
    }

    public void processingPlaytimeInfo(boolean processing) {
        this.processingPlaytimeInfo = processing;
    }

    public void requestSent() {
        this.awaitingResponse = true;
        this.ticksSinceRequest = 0;
    }

    public void responseReceived() {
        this.awaitingResponse = false;
        this.ticksSinceRequest = 0;
    }

    public void tickWaiting() {
        if (awaitingResponse) {
            ticksSinceRequest++;
        }
    }

    public boolean waitedLongerThan(int ticks) {
        if (!awaitingResponse) {
            return false;
        }
        return ticksSinceRequest > ticks;
    }

    public void resetParsing() {
        processingPlaytimeInfo = false;
        lastKnownLocation = null;
        responseReceived();
    }

    public Optional<ServerLocation> knownLocation() {
        return Optional.ofNullable(lastKnownLocation);
    }

    public boolean isOnSameServer(ServerLocation userLocation) {
        if (userLocation == null) {
            return false;
        }
        return status.isOnSameServerAs(userLocation);
    }

    public boolean isRightHere(ServerLocation userLocation) {
        if (!isOnSameServer(userLocation)) {
            return false;
        }
        return activity.isEmpty();
    }
}
