package me.zyouime.holymoderation.core.service;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ReportService(ChatService chatService, NotificationsService notificationsService) {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("Дело игрока\\s+(?:«[^»]+»\\s+)?([^\\s(\\[]+)");

    public void handleScreenClick(HandledScreen<?> screen, Slot slot) {
        String title = screen.getTitle().getString();
        if (!title.startsWith("Жалобы")) {
            return;
        }
        if (!slot.hasStack()) {
            return;
        }
        ItemStack stack = slot.getStack();
        if (stack.getName() == null) {
            return;
        }
        String itemStackName = stack.getName().getString();
        Matcher matcher = NICKNAME_PATTERN.matcher(itemStackName);
        if (matcher.find()) {
            String suspectNickname = matcher.group(1);
            chatService.copyToClipboard(suspectNickname);
            notificationsService.success("Скопирован ник %s".formatted(suspectNickname));
        }
    }
}
