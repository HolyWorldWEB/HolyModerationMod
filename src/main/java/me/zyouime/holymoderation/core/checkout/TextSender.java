package me.zyouime.holymoderation.core.checkout;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.core.service.ChatService;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public final class TextSender {

    private static final int INTERVAL_TICKS = 2;
    private final ChatService chatService;
    private List<String> texts = List.of();
    private String target = StringUtils.EMPTY;
    private int index = 0;
    private int ticksUntilNext = -1;

    public void send(String player, List<String> texts) {
        this.texts = List.copyOf(texts);
        this.target = player;
        this.index = 0;
        this.ticksUntilNext = 1;
    }

    public void cancel() {
        texts = List.of();
        target = StringUtils.EMPTY;
        index = 0;
        ticksUntilNext = -1;
    }

    public void tick() {
        if (ticksUntilNext < 0) {
            return;
        }
        ticksUntilNext--;
        if (ticksUntilNext > 0) {
            return;
        }
        if (index >= texts.size()) {
            cancel();
            return;
        }
        String text = texts.get(index).replace("§", "&");
        chatService.chatMessage("/msg %s %s".formatted(target, text));
        index++;
        ticksUntilNext = INTERVAL_TICKS;
    }
}
