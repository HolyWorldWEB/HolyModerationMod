package me.zyouime.holymoderation.core.nvp;

import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.NVPService;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record NVPChatListener(NVPService nvpService) {

    private static final Pattern NVP_START = Pattern.compile("^▶ Начато наблюдение за игроком (\\w+)");

    public void onMessage(Text message) {
        String string = ChatService.stripColor(message.getString());
        if (HolyWorldPatterns.messageSender(string) != null) {
            return;
        }
        Matcher matcher = NVP_START.matcher(string);
        if (matcher.matches()) {
            nvpService.nvpStart(matcher.group(1));
        }
    }
}
