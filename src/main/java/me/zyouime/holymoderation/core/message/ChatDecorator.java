package me.zyouime.holymoderation.core.message;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public record ChatDecorator(CheckoutService checkoutService, ChatService chatService, ModSettings settings) {

    private static final String MOD_PREFIX = "[HM]";
    private static final String COPY_HINT = "Нажмите, чтобы скопировать сообщение.";
    private static final String SUSPECT_HINT = "Сообщение проверяемого игрока.\nНажмите, чтобы скопировать.";

    public Text decorate(Text message) {
        String plain = ChatService.stripColor(message.getString());
        if (isFromSuspect(plain)) {
            return markSuspectMessage(message, plain);
        }
        if (settings.copyButton.getValue() && !plain.startsWith(MOD_PREFIX)) {
            return appendCopyButton(message, plain);
        }
        return message;
    }

    private boolean isFromSuspect(String plain) {
        String suspect = checkoutService.suspect();
        return !suspect.isEmpty() && suspect.equals(HolyWorldPatterns.chatSender(plain));
    }

    private Text markSuspectMessage(Text message, String plain) {
        MutableText marker = Text.literal("%s ".formatted(settings.playerMarker.getValue()));
        return chatService.copyTextComponent(chatService.generateComponent(marker, message), SUSPECT_HINT, plain);
    }

    private Text appendCopyButton(Text message, String plain) {
        MutableText button = Text.literal(" ").append(chatService.copyTextComponent(settings.copyButtonText.getValue(), COPY_HINT, plain));
        return chatService.generateComponent(message, button);
    }
}