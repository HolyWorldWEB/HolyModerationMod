package me.zyouime.holymoderation.core.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.checkout.CheckoutPrompts;
import me.zyouime.holymoderation.core.checkout.CheckoutSession;
import me.zyouime.holymoderation.core.checkout.TextSender;
import me.zyouime.holymoderation.core.fabric.events.checkout.CheckoutEvents;
import me.zyouime.holymoderation.core.states.UserState;
import me.zyouime.holymoderation.core.user.VanishController;

@RequiredArgsConstructor
public final class CheckoutService {

    private static final int TICKS_PER_SECOND = 20;
    private static final int SEND_TEXTS_TICK = TICKS_PER_SECOND * 5;
    private static final int GATHER_INFO_TICK = TICKS_PER_SECOND * 6;
    private static final int JOURNAL_PROMPT_TICK = TICKS_PER_SECOND * 7;
    private static final int FREEZE_TIMEOUT_TICKS = TICKS_PER_SECOND * 5;
    private static final int END_PROMPT_DELAY_TICKS = TICKS_PER_SECOND;
    private final UserState userState;
    private final ChatService chatService;
    private final NotificationsService notifications;
    private final SpyService spyService;
    private final CheckoutPrompts prompts;
    private final TextSender textSender;
    private final ModSettings settings;
    private final VanishController vanish;
    private CheckoutSession session;
    private String endPromptSuspect = "";
    private int ticksUntilEndPrompt = -1;

    public boolean isChecking() {
        return session != null;
    }

    public Optional<CheckoutSession> session() {
        return Optional.ofNullable(session);
    }

    public String suspect() {
        if (session == null) {
            return "";
        }
        return session.getSuspect();
    }

    public boolean isSuspect(String player) {
        if (session == null) {
            return false;
        }
        return session.getSuspect().equalsIgnoreCase(player);
    }

    public boolean isAwaitingFreeze() {
        if (session == null) {
            return false;
        }
        return session.isAwaitingFreeze();
    }

    public void start(String player) {
        if (session != null) {
            notifications.error("Вы уже проверяете игрока %s. Сначала закончите текущую проверку: /hm unfrz".formatted(session.getSuspect()));
            return;
        }
        session = new CheckoutSession(player);
        chatService.chatMessage("/freezing %s".formatted(player));
    }

    public void finish() {
        if (session == null) {
            return;
        }
        String suspect = endSession(true);
        notifications.success("Вы успешно закончили проверку.");
        scheduleEndPrompt(suspect);
    }

    public void finishAfterBan() {
        if (session == null) {
            return;
        }
        String suspect = endSession(false);
        notifications.success("Вы успешно закончили проверку.");
        scheduleEndPrompt(suspect);
    }

    public void cancelPlayerNotFound() {
        if (session == null) {
            return;
        }
        endSession(false);
        notifications.warning("Проверка отменена, потому что игрок не был найден.");
    }

    public void onSuspectLeft() {
        if (session == null) {
            return;
        }
        String suspect = endSession(false);
        notifications.warning("Игрок %s вышел с проверки. Проверка завершена.".formatted(suspect));
        scheduleEndPrompt(suspect);
    }

    public void sendTexts(String player) {
        List<String> texts = settings.checkoutTexts.getValue();
        if (texts.isEmpty()) {
            notifications.error("У вас нет настроенных текстов. Добавить: /hm texts add <текст>");
            return;
        }
        textSender.send(player, texts);
    }

    public void tick() {
        tickSession();
        tickEndPrompt();
        textSender.tick();
    }

    public void onFreezeConfirmed() {
        if (session == null || !session.isAwaitingFreeze()) {
            return;
        }
        session.freezeAnswered();
        beginCheckout();
    }

    public void onFreezeFailed() {
        if (session == null || !session.isAwaitingFreeze()) {
            return;
        }
        session.freezeAnswered();
        cancelPlayerNotFound();
    }

    public void onServerLeave() {
        session = null;
        endPromptSuspect = "";
        ticksUntilEndPrompt = -1;
        textSender.cancel();
    }

    private void beginCheckout() {
        String suspect = session.getSuspect();
        session.markStarted();
        if (settings.autoCheckoutTp.getValue()) {
            chatService.chatMessage("/warp logo");
        }
        chatService.chatMessage("/prova");
        notifications.success("Вы успешно начали проверку.");
        CheckoutEvents.STARTED.invoker().onStarted(suspect);
    }

    private String endSession(boolean sendUnfreeze) {
        String suspect = session.getSuspect();
        boolean started = session.isStarted();
        if (started) {
            chatService.chatMessage("/prova");
        }
        if (sendUnfreeze) {
            chatService.chatMessage("/freezing %s".formatted(suspect));
        }
        if (started) {
            restoreModeratorState();
        }
        stopSpyingSuspect(suspect);
        session = null;
        textSender.cancel();
        if (started) {
            CheckoutEvents.FINISHED.invoker().onFinished(suspect);
        }
        return suspect;
    }

    private void scheduleEndPrompt(String suspect) {
        endPromptSuspect = suspect;
        ticksUntilEndPrompt = END_PROMPT_DELAY_TICKS;
    }

    private void tickSession() {
        if (session == null) {
            return;
        }
        session.tick();
        if (session.waitedLongerThan(FREEZE_TIMEOUT_TICKS)) {
            session.freezeAnswered();
            notifications.warning("Сервер не подтвердил заморозку. Проверка продолжена, проверьте состояние игрока вручную.");
            beginCheckout();
            return;
        }
        if (session.isAtTick(SEND_TEXTS_TICK)) {
            sendTexts(session.getSuspect());
            return;
        }
        if (session.isAtTick(GATHER_INFO_TICK)) {
            gatherInfo(session.getSuspect());
            return;
        }
        if (session.isAtTick(JOURNAL_PROMPT_TICK)) {
            prompts.showJournalPrompt(session.getSuspect());
        }
    }

    private void tickEndPrompt() {
        if (ticksUntilEndPrompt < 0) {
            return;
        }
        ticksUntilEndPrompt--;
        if (ticksUntilEndPrompt > 0) {
            return;
        }
        ticksUntilEndPrompt = -1;
        prompts.showEndPrompt(endPromptSuspect);
        endPromptSuspect = "";
    }

    private void gatherInfo(String suspect) {
        if (settings.dupeIp.getValue()) {
            chatService.chatMessage("/dupeip %s".formatted(suspect));
        }
        chatService.chatMessage("/checkmute %s".formatted(suspect));
        if (settings.autoVanish.getValue()) {
            vanish.set(false);
        }
        if (settings.autoGm3.getValue() && userState.isGm3Enabled()) {
            chatService.chatMessage("/gm 0");
            userState.setGm3Enabled(false);
        }
    }

    private void restoreModeratorState() {
        if (settings.autoVanish.getValue()) {
            vanish.set(true);
        }
        if (settings.autoGm3.getValue() && !userState.isGm3Enabled()) {
            chatService.chatMessage("/gm 3");
            userState.setGm3Enabled(true);
        }
    }

    private void stopSpyingSuspect(String suspect) {
        boolean spyingSuspect = spyService.session().map(spy -> spy.getPlayer().equalsIgnoreCase(suspect)).orElse(false);
        if (spyingSuspect) {
            spyService.endSpy();
        }
    }
}