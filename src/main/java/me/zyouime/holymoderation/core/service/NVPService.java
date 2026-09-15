package me.zyouime.holymoderation.core.service;

public record NVPService(SpyService spyService, ChatService chatService, NotificationsService notificationsService) {

    public void nvpStart(String nickname) {
        if (spyService.isSpying()) {
            spyService.endSpy(false);
            notificationsService.warning("Текущая слежка была остановлена. Переключение на слежку по NVP");
        }
        spyService.startSpy(nickname);
        chatService.copyToClipboard(nickname);
    }
}
