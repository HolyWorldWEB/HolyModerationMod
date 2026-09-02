package me.zyouime.holymoderation.core.user;

import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.states.UserState;

public record VanishController(UserState userState, ChatService chatService) {

    public boolean isAvailable() {
        return MinecraftProvider.isSpawnWorld();
    }

    public void set(boolean target) {
        if (!isAvailable() || target == userState.isVanishEnabled()) {
            return;
        }
        chatService.chatMessage("/v");
        userState.setVanishEnabled(target);
    }

    public void syncOnEnter() {
        if (isAvailable()) {
            userState.setVanishEnabled(true);
        }
    }

    public void onManualToggle(boolean target) {
        if (isAvailable()) {
            userState.setVanishEnabled(target);
        }
    }
}