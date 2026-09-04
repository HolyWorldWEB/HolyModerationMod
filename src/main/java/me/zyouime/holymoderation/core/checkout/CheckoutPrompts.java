package me.zyouime.holymoderation.core.checkout;

import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.util.Colors;
import net.minecraft.text.MutableText;

public record CheckoutPrompts(ChatService chatService) {

    public void showJournalPrompt(String suspect) {
        for (CheckoutReason reason : CheckoutReason.values()) {
            send("Внести проверку: %s".formatted(reason.getLabel()),
                    "Нажмите, чтобы внести проверку в журнал",
                    "/hm startcheckout %s %s".formatted(suspect, reason.getApiValue()));
        }
    }

    public void showEndPrompt() {
        for (CheckoutResult result : CheckoutResult.values()) {
            if (!result.isNeedsStashChoice()) {
                send("Закончить: %s".formatted(result.getLabel()),
                        "Нажмите, чтобы закончить проверку",
                        "/hm endcheckout %s".formatted(result.getApiValue()));
                continue;
            }
            sendStashOptions(result);
        }
    }

    public void showBanReasonPrompt(boolean destroyStash) {
        send("Дописать причину бана вручную",
                "Нажмите, чтобы подставить команду и дописать причину",
                "/hm endcheckout ban %s ".formatted(destroyStash));
    }

    private void sendStashOptions(CheckoutResult result) {
        send("Закончить: %s + снести стеш".formatted(result.getLabel()),
                "Нажмите, чтобы закончить проверку со сносом стеша",
                "/hm endcheckout %s true".formatted(result.getApiValue()));
        send("Закончить: %s + не сносить стеш".formatted(result.getLabel()),
                "Нажмите, чтобы закончить проверку без сноса стеша",
                "/hm endcheckout %s false".formatted(result.getApiValue()));
    }

    private void send(String label, String hint, String command) {
        MutableText component = chatService.suggestTextComponent("%s%s%s".formatted(Colors.AQUA, Colors.BOLD, label), hint, command);
        chatService.clientMessage(component);
    }
}