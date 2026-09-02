package me.zyouime.holymoderation.core.user;

import me.zyouime.holymoderation.core.command.CommandInitializer;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.states.UserState;

public record SelfStateTracker(UserState userState, NotificationsService notifications, VanishController vanish) {

    private static final String ENABLE = "enable";
    private static final String DISABLE = "disable";

    public void onCommand(String command) {
        if (!userState.isOnHW()) {
            return;
        }
        if (command.startsWith(CommandInitializer.ROOT)) {
            return;
        }
        if (!userState.isGameInitCompleted()) {
            return;
        }
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "v" -> handleVanish(parts);
            case "gamemode", "gm" -> handleGameMode(parts);
            case "fly" -> userState.setFlyEnabled(resolveToggle(parts, userState.isFlyEnabled()));
            case "god" -> userState.setGodEnabled(resolveToggle(parts, userState.isGodEnabled()));
            case "hac" -> handleHacAlerts(parts);
        }
    }

    private void handleVanish(String[] parts) {
        vanish.onManualToggle(resolveToggle(parts, userState.isVanishEnabled()));
    }

    private void handleGameMode(String[] parts) {
        if (parts.length < 2) {
            return;
        }
        String mode = parts[1];
        if (mode.equals("3") || mode.equals("spectator")) {
            userState.setGm3Enabled(true);
            return;
        }
        if (mode.equals("0") || mode.equals("1") || mode.equals("2") || mode.equals("survival") || mode.equals("creative") || mode.equals("adventure")) {
            userState.setGm3Enabled(false);
        }
    }

    private void handleHacAlerts(String[] parts) {
        if (parts.length < 2 || !parts[1].equals("alerts")) {
            return;
        }
        userState.setHacAlertsEnabled(!userState.isHacAlertsEnabled());
    }

    private boolean resolveToggle(String[] parts, boolean current) {
        if (parts.length > 1) {
            if (parts[1].equals(ENABLE)) {
                return true;
            }
            if (parts[1].equals(DISABLE)) {
                return false;
            }
        }
        return !current;
    }
}