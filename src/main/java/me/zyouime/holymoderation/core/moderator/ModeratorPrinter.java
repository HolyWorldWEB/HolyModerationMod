package me.zyouime.holymoderation.core.moderator;

import java.time.Duration;
import java.time.Instant;

import me.zyouime.holymoderation.core.dto.JournalProfile;
import me.zyouime.holymoderation.core.dto.JournalStats;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.states.ModeratorState;
import me.zyouime.holymoderation.core.util.Colors;
import net.minecraft.text.Text;

public record ModeratorPrinter(ModeratorState state, ChatService chatService) {

    public void printProfile() {
        JournalProfile data = state.getProfile();
        if (data == null) {
            line("Профиль ещё не загружен.");
            return;
        }
        line("%s%sПрофиль%s %s (%s)".formatted(Colors.AQUA, Colors.BOLD, Colors.WHITE, data.nickname(), data.fullname()));
        line("Ранг: %d | выговоры: %d | варны: %d | непонятки: %d".formatted(data.rank(), data.reprimands(), data.warns(), data.neponyatki()));
        line("Режим: %s | анархия: %d".formatted(data.anarchyMode(), data.anarchy()));
        printAge(state.getProfileUpdatedAt());
    }

    public void printStats() {
        JournalStats data = state.getStats();
        if (data == null) {
            line("Статистика ещё не загружена.");
            return;
        }
        line("%s%sСтатистика%s".formatted(Colors.AQUA, Colors.BOLD, Colors.WHITE));
        line("Сегодня: муты %d | баны %d | гаранты %d".formatted(data.mutesToday(), data.bansToday(), data.gaurantsToday()));
        line("Месяц: муты %d | гаранты %d".formatted(data.mutesMonth(), data.gaurantsMonth()));
        line("Всего: муты %d | гаранты %d".formatted(data.mutesAll(), data.gaurantsAll()));
        printAge(state.getStatsUpdatedAt());
    }

    private void printAge(Instant updatedAt) {
        if (updatedAt == null) {
            return;
        }
        long minutes = Duration.between(updatedAt, Instant.now()).toMinutes();
        if (minutes < 1) {
            line("%sОбновлено только что".formatted(Colors.GRAY));
            return;
        }
        line("%sОбновлено %d мин. назад".formatted(Colors.GRAY, minutes));
    }

    private void line(String text) {
        chatService.clientMessage(Text.of(text));
    }
}
