package me.zyouime.holymoderation.core.checkout;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

import me.zyouime.holymoderation.core.api.JournalApi;
import me.zyouime.holymoderation.core.dto.CheckoutEndRequest;
import me.zyouime.holymoderation.core.dto.CheckoutRequest;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.service.ModeratorService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.spy.ServerLocation;
import me.zyouime.holymoderation.core.spy.ServerType;
import me.zyouime.holymoderation.core.states.UserState;

public record CheckoutJournal(JournalApi journalApi, UserState userState, ModeratorService moderatorService, NotificationsService notifications, LoggerService logger) {

    private static final int LPVP_SERVER_NUMBER = 1;

    public void start(String player, CheckoutReason reason) {
        ServerLocation location = userState.getUserLocation();
        if (location == null) {
            notifications.error("Локация не определена, проверку внести нельзя.");
            return;
        }
        CheckoutRequest request = toRequest(player, reason, location);
        run(true, "Проверка внесена в журнал.", "У вас уже есть активная проверка в журнале.", () -> journalApi.startCheckout(request));
    }

    public void end(CheckoutResult result, String banReason, boolean destroyStash) {
        CheckoutEndRequest request = new CheckoutEndRequest(result.getApiValue(), banReason, destroyStash);
        run(false, "Проверка завершена в журнале.", "У вас нет активной проверки в журнале.", () -> journalApi.endCheckout(request).thenRun(() -> moderatorService.refreshStats(false)));
    }

    private void run(boolean expectNoActive, String successMessage, String conflictMessage, Supplier<CompletableFuture<Void>> action) {
        journalApi.hasActiveCheckout().thenCompose(active -> {
                    if (active == expectNoActive) {
                        notifications.error(conflictMessage);
                        return CompletableFuture.completedFuture(null);
                    }
                    return action.get().thenRun(() -> notifications.success(successMessage));
                })
                .exceptionally(throwable -> {
                    notifications.error("Ошибка журнала: %s".formatted(throwable.getMessage()));
                    return null;
                });
    }

    private CheckoutRequest toRequest(String player, CheckoutReason reason, ServerLocation location) {
        if (location.type() == ServerType.LPVP) {
            return new CheckoutRequest(player, reason.getApiValue(), ServerType.LITE.getLabel(), LPVP_SERVER_NUMBER, true);
        }
        return new CheckoutRequest(player, reason.getApiValue(), location.type().getLabel(), location.number(), false);
    }
}
