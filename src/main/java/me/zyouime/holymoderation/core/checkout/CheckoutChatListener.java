package me.zyouime.holymoderation.core.checkout;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public record CheckoutChatListener(CheckoutService checkoutService, BanReasonLookup banReasonLookup, AnyDeskExtractor anyDeskExtractor, ChatService chatService, ModSettings settings) {

    public ActionResult onMessage(Text message) {
        String text = chatService.formatReceivedText(message.getString());
        if (text == null) {
            return ActionResult.PASS;
        }
        if (checkoutService.isAwaitingFreeze()) {
            handleFreezeResponse(text);
        }
        if (checkoutService.isChecking() && HolyWorldPatterns.isFreezeLeave(text, checkoutService.suspect())) {
            checkoutService.onSuspectLeft();
            return ActionResult.PASS;
        }
        if (settings.autoAnyDesk.getValue() && checkoutService.isChecking()) {
            anyDeskExtractor.inspect(text, checkoutService.suspect());
        }
        if (banReasonLookup.onMessage(text)) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private void handleFreezeResponse(String text) {
        if (text.equals(HolyWorldPatterns.FREEZE_OK)) {
            checkoutService.onFreezeConfirmed();
            return;
        }
        if (text.equals(HolyWorldPatterns.FREEZE_NOT_FOUND)) {
            checkoutService.onFreezeFailed();
        }
    }
}