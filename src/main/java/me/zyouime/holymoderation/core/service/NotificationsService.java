package me.zyouime.holymoderation.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.core.notification.Notification;
import me.zyouime.holymoderation.core.notification.NotificationType;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.util.Colors;

import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

@RequiredArgsConstructor
public final class NotificationsService {

    public static final int TICKS_PER_SECOND = 20;
    public static final int SPAWN_TICKS = 6;
    public static final int HIDE_TICKS = 8;
    private static final int DEFAULT_LIFE_TICKS = TICKS_PER_SECOND * 5;
    private static final int MAX_NOTIFICATIONS = 8;
    private final Queue<Notification> pending = new ConcurrentLinkedQueue<>();
    private final List<Notification> active = new ArrayList<>();

    public void add(NotificationType type, String title, String text, int lifeTicks) {
        pending.add(new Notification(type, title, text, lifeTicks, false));
    }

    public void success(String text) {
        success(text, DEFAULT_LIFE_TICKS);
    }

    public void success(String text, int lifeTicks) {
        add(NotificationType.SUCCESS, title(Colors.GREEN, "Успех"), text, lifeTicks);
    }

    public void warning(String text) {
        warning(text, DEFAULT_LIFE_TICKS);
    }

    public void warning(String text, int lifeTicks) {
        add(NotificationType.WARNING, title(Colors.GOLD, "Предупреждение"), text, lifeTicks);
    }

    public void error(String text) {
        error(text, DEFAULT_LIFE_TICKS);
    }

    public void error(String text, int lifeTicks) {
        add(NotificationType.ERROR, title(Colors.RED, "Ошибка"), text, lifeTicks);
    }

    public void clear() {
        pending.clear();
        active.clear();
    }

    public List<Notification> notifications() {
        return Collections.unmodifiableList(active);
    }

    public boolean isEmpty() {
        return active.isEmpty();
    }

//    public void showPreview() {
//        boolean alreadyShown = active.stream().anyMatch(Notification::isPersistent);
//        if (alreadyShown) {
//            return;
//        }
//        pending.add(new Notification(
//                NotificationType.SUCCESS,
//                title(Colors.GREEN, "Пример уведомления"),
//                "Скролл меняет размер уведомлений",
//                0,
//                true));
//    }
//
//    public void hidePreview() {
//        active.removeIf(Notification::isPersistent);
//        pending.removeIf(Notification::isPersistent);
//    }

    public void tick() {
        drainPending();
        for (Notification notification : active) {
            notification.tick();
            advance(notification);
        }
        active.removeIf(notification -> notification.isHidden(HIDE_TICKS));
    }

    private void drainPending() {
        Notification notification = pending.poll();
        while (notification != null) {
            active.add(notification);
            notification = pending.poll();
        }
        while (countVisible() > MAX_NOTIFICATIONS) {
            hideOldestVisible();
        }
    }

    private void advance(Notification notification) {
        if (notification.getState() == Notification.State.SPAWNING && notification.getElapsedTicks() >= SPAWN_TICKS) {
            notification.state(Notification.State.IDLE);
            return;
        }
        if (notification.isExpired()) {
            notification.state(Notification.State.HIDING);
        }
    }

    private int countVisible() {
        int count = 0;
        for (Notification notification : active) {
            if (notification.isVisible()) {
                count++;
            }
        }
        return count;
    }

    private void hideOldestVisible() {
        for (Notification notification : active) {
            if (notification.isVisible() && !notification.isPersistent()) {
                notification.state(Notification.State.HIDING);
                return;
            }
        }
    }

    private String title(String color, String text) {
        return "%s%s%s".formatted(color, Colors.BOLD, text);
    }
}
