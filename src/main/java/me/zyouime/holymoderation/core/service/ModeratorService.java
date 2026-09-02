package me.zyouime.holymoderation.core.service;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.api.JournalApi;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.states.ModeratorState;

@RequiredArgsConstructor
public final class ModeratorService {

    private final JournalApi journalApi;
    private final ModeratorState state;
    private final ModSettings settings;
    private final NotificationsService notifications;
    private boolean loadingProfile = false;
    private boolean loadingStats = false;

    public void refreshAll() {
        refreshProfile(false);
        refreshStats(false);
    }

    public void refreshProfile(boolean notifyUser) {
        if (canRequest(notifyUser) || loadingProfile) {
            return;
        }
        loadingProfile = true;
        journalApi.profile()
                .thenAccept(profile -> onMainThread(profile, state::profile))
                .whenComplete((ignored, throwable) -> loadingProfile = false);
    }

    public void refreshStats(boolean notifyUser) {
        if (canRequest(notifyUser) || loadingStats) {
            return;
        }
        loadingStats = true;
        journalApi.stats()
                .thenAccept(stats -> onMainThread(stats, state::stats))
                .whenComplete((ignored, throwable) -> loadingStats = false);
    }

    public void onTokenChanged() {
        state.reset();
        refreshAll();
    }

    private boolean canRequest(boolean notifyUser) {
        if (!settings.apiToken.getValue().isEmpty()) {
            return false;
        }
        if (notifyUser) {
            notifications.warning("Токен журнала не указан.");
        }
        return true;
    }

    private <T> void onMainThread(T value, Consumer<T> action) {
        MinecraftProvider.client().execute(() -> action.accept(value));
    }
}
