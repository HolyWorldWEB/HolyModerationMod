package me.zyouime.holymoderation.core.user;

import lombok.Getter;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.states.UserState;

public final class UserLocator {

    private static final int TICKS_PER_SECOND = 20;
    private static final int MAX_ATTEMPTS = 15;
    private final UserState userState;
    private final ChatService chatService;
    @Getter
    private boolean searching = false;
    private int ticksUntilRetry = -1;
    private int attempts = 0;

    public UserLocator(UserState userState, ChatService chatService) {
        this.userState = userState;
        this.chatService = chatService;
    }

    public void start() {
        searching = true;
        attempts = 0;
        ticksUntilRetry = TICKS_PER_SECOND;
    }

    public void stop() {
        searching = false;
        ticksUntilRetry = -1;
        attempts = 0;
    }

    public void tick() {
        if (!searching) {
            return;
        }
        if (userState.hasLocation() || userState.isInHub()) {
            stop();
            return;
        }
        if (ticksUntilRetry < 0) {
            return;
        }
        ticksUntilRetry--;
        if (ticksUntilRetry > 0) {
            return;
        }
        attempts++;
        if (attempts > MAX_ATTEMPTS) {
            stop();
            return;
        }
        ticksUntilRetry = TICKS_PER_SECOND;
        chatService.chatMessage("/find %s".formatted(userState.getUserNickname()));
    }
}
