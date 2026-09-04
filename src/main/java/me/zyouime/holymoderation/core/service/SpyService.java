package me.zyouime.holymoderation.core.service;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.spy.ServerLocation;
import me.zyouime.holymoderation.core.spy.SpySession;
import me.zyouime.holymoderation.core.spy.SpyStatus;
import me.zyouime.holymoderation.core.states.UserState;

import org.apache.commons.lang3.StringUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SpyService {

    public static final int TICKS_PER_SECOND = 20;
    public static final int INSTANT_DELAY_TICKS = 10;
    private static final int START_DELAY_TICKS = 5;
    private static final int RESPONSE_TIMEOUT_TICKS = TICKS_PER_SECOND * 5;
    private final UserState userState;
    private final ChatService chatService;
    private final ModSettings settings;
    private final NotificationsService notificationsService;
    private SpySession session;
    private int ticksUntilUpdate = -1;

    public void startSpy(String player) {
        session = new SpySession(player);
        scheduleUpdate(START_DELAY_TICKS);
        notificationsService.success("Слежка начата");
    }

    public void endSpy() {
        if (session == null) {
            return;
        }
        session = null;
        ticksUntilUpdate = -1;
        notificationsService.success("Слежка остановлена.");
    }

    public boolean isSpying() {
        return session != null;
    }

    public Optional<SpySession> session() {
        return Optional.ofNullable(session);
    }

    public SpySession sessionOrNull() {
        return session;
    }

    public void scheduleUpdate(int delayTicks) {
        if (session == null) {
            return;
        }
        ticksUntilUpdate = Math.max(1, delayTicks);
    }

    public void scheduleUpdate() {
        scheduleUpdate(settings.spyDelay.getValue() * TICKS_PER_SECOND);
    }

    public void tick() {
        if (session == null) {
            return;
        }
        session.tickWaiting();
        if (session.waitedLongerThan(RESPONSE_TIMEOUT_TICKS)) {
            session.responseReceived();
            session.resetParsing();
        }
        if (ticksUntilUpdate < 0) {
            return;
        }
        ticksUntilUpdate--;
        if (ticksUntilUpdate > 0) {
            return;
        }
        scheduleUpdate();
        update();
    }

    public void onServerSwitch() {
        if (session == null) {
            return;
        }
        session.resetParsing();
    }

    public void update() {
        if (session == null) {
            return;
        }
        if (userState.isInHub() && session.getStatus().type() != SpyStatus.Type.PAUSED) {
            onPause();
        }
        if (userState.isInHub() || !userState.hasLocation()) {
            return;
        }
        session.requestSent();
        if (session.isOnSameServer(userLocation())) {
            chatService.chatMessage("/playtime %s".formatted(session.getPlayer()));
            return;
        }
        chatService.chatMessage("/find %s".formatted(session.getPlayer()));
    }

    public ServerLocation userLocation() {
        return userState.getUserLocation();
    }

    public void onFindResponse(SpyStatus status) {
        if (session == null) {
            return;
        }
        session.status(status);
        session.activity(StringUtils.EMPTY);
        session.responseReceived();
    }

    public void onPlaytimeComplete() {
        if (session == null) {
            return;
        }
        session.responseReceived();
    }

    public void onPause() {
        if (session == null) {
            return;
        }
        session.status(SpyStatus.paused());
        session.activity(StringUtils.EMPTY);
        session.resetParsing();
        notificationsService.success("Слежка приостановлена.");
    }
}
