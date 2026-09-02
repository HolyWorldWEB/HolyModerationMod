package me.zyouime.holymoderation.core.checkout;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public final class BanReasonLookup {

    private static final int TIMEOUT_TICKS = 20 * 5;
    private final ChatService chatService;
    private final NotificationsService notifications;
    @Getter
    private boolean active = false;
    private boolean insideInfoBlock = false;
    private int ticksWaiting = 0;
    private String reason = StringUtils.EMPTY;
    private Callback callback;
    private Runnable onFailure;

    public void request(String player, Callback callback, Runnable onFailure) {
        this.active = true;
        this.insideInfoBlock = false;
        this.ticksWaiting = 0;
        this.reason = StringUtils.EMPTY;
        this.callback = callback;
        this.onFailure = onFailure;
        chatService.chatMessage("/checkban %s".formatted(player));
    }

    public void cancel() {
        active = false;
        insideInfoBlock = false;
        ticksWaiting = 0;
        reason = StringUtils.EMPTY;
        callback = null;
        onFailure = null;
    }

    public void tick() {
        if (!active) {
            return;
        }
        ticksWaiting++;
        if (ticksWaiting <= TIMEOUT_TICKS) {
            return;
        }
        fail("Сервер не ответил на /checkban.");
    }

    public boolean onMessage(String text) {
        if (!active) {
            return false;
        }
        if (HolyWorldPatterns.isCheckbanAbort(text)) {
            fail("Не удалось определить причину бана.");
            return true;
        }
        if (text.startsWith(HolyWorldPatterns.CHECKBAN_INFO_PREFIX)) {
            insideInfoBlock = true;
        }
        String parsed = HolyWorldPatterns.extractBanReason(text);
        if (parsed != null) {
            reason = parsed;
        }
        boolean hide = insideInfoBlock;
        if (text.startsWith(HolyWorldPatterns.CHECKBAN_IPBAN_PREFIX)) {
            complete();
        }
        return hide;
    }

    private void complete() {
        Callback finished = callback;
        String found = reason;
        cancel();
        if (finished != null) {
            finished.onReason(found);
        }
    }

    private void fail(String message) {
        Runnable failure = onFailure;
        cancel();
        notifications.error(message);
        if (failure != null) {
            failure.run();
        }
    }

    public interface Callback {
        void onReason(String reason);
    }
}
