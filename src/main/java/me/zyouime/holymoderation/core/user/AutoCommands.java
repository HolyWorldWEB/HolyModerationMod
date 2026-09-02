package me.zyouime.holymoderation.core.user;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.states.UserState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;

public record AutoCommands(UserState userState, ChatService chatService, ModSettings settings, VanishController vanish) {

    public void applyAfterSwitch() {
        syncStateFromGame();
        vanish.set(settings.autoVanish.getValue());
        applyGameMode3();
        applyFly();
        applyGod();
        applyHacAlerts();
    }

    private void syncStateFromGame() {
        vanish.syncOnEnter();
        ClientPlayerInteractionManager interactionManager = MinecraftProvider.client().interactionManager;
        if (interactionManager == null) {
            return;
        }
        userState.setGm3Enabled(interactionManager.getCurrentGameMode() == GameMode.SPECTATOR);
    }

    private void applyGameMode3() {
        boolean target = settings.autoGm3.getValue();
        if (target == userState.isGm3Enabled()) {
            return;
        }
        chatService.chatMessage(target ? "/gm 3" : "/gm 0");
        userState.setGm3Enabled(target);
    }

    private void applyFly() {
        boolean target = settings.autoFly.getValue();
        if (target == userState.isFlyEnabled() || userState.isGm3Enabled()) {
            return;
        }
        chatService.chatMessage("/fly");
        userState.setFlyEnabled(target);
    }

    private void applyGod() {
        boolean target = settings.autoGod.getValue();
        if (target == userState.isGodEnabled()) {
            return;
        }
        chatService.chatMessage("/god");
        userState.setGodEnabled(target);
    }

    private void applyHacAlerts() {
        boolean target = settings.autoHacAlerts.getValue();
        if (target == userState.isHacAlertsEnabled()) {
            return;
        }
        chatService.chatMessage("/hac alerts");
        userState.setHacAlertsEnabled(target);
    }
}