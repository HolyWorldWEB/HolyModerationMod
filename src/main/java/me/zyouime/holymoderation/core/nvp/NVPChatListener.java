package me.zyouime.holymoderation.core.nvp;

import me.zyouime.holymoderation.core.service.NVPService;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record NVPChatListener(NVPService nvpService) {

    private static final Pattern NVP_START = Pattern.compile("▶ Начато наблюдение за игроком (\\w+)");

    public void onMessage(Text message) {
        String string = message.getString();
        if (string== null ) {
            return;
        }
        Matcher matcher = NVP_START.matcher(string);
        if (matcher.find()) {
            nvpService.nvpStart(matcher.group(1));
        }
    }
}
