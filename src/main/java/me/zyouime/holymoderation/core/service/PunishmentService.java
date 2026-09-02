package me.zyouime.holymoderation.core.service;

import java.util.Optional;

import me.zyouime.holymoderation.core.punishment.PunishmentTime;
import me.zyouime.holymoderation.core.punishment.PunishmentType;
import me.zyouime.holymoderation.core.punishment.VkLinkProvider;

public record PunishmentService(ChatService chatService, NotificationsService notifications, VkLinkProvider vkLinkProvider) {

    private static final String SILENT_FLAG = "-s";
    private static final String VK_SUFFIX = "| Вопросы? %s";

    public boolean punish(PunishmentType type, String player, String reason, boolean addVk) {
        if (!addVk) {
            chatService.chatMessage("/%s %s %s %s".formatted(type.getCommand(), player, reason, SILENT_FLAG));
            return true;
        }
        Optional<String> link = vkLinkProvider.link();
        if (link.isEmpty()) {
            notifications.error("Ссылка на ВК неизвестна. Укажите её: /hm setvk <ссылка>");
            return false;
        }
        if (vkLinkProvider.isFallback()) {
            notifications.warning("Профиль журнала не загружен, использована ссылка из настроек.");
        }
        chatService.chatMessage("/%s %s %s %s %s".formatted(type.getCommand(), player, reason, VK_SUFFIX.formatted(link.get()), SILENT_FLAG));
        return true;
    }

    public boolean punish(PunishmentType type, String player, String time, String reason, boolean addVk) {
        if (!PunishmentTime.isValid(time)) {
            notifications.error("Неверный формат времени. Должно быть 1-9999s, 1-9999m, 1-9999h или 1-9999d");
            return false;
        }
        return punish(type, player, "%s %s".formatted(time, reason), addVk);
    }
}
