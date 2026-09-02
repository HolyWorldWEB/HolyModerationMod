package me.zyouime.holymoderation.core.checkout;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.NotificationsService;

@RequiredArgsConstructor
public final class AnyDeskExtractor {

    private static final int MIN_LENGTH = 7;
    private static final int MAX_LENGTH = 11;
    private static final Pattern CANDIDATE = Pattern.compile("(?<!\\d)\\d{1,3}(?:[ \\u00A0.\\-]\\d{3})+(?!\\d)|(?<!\\d)\\d{" + MIN_LENGTH + "," + MAX_LENGTH + "}(?!\\d)");
    private static final Pattern NON_DIGIT = Pattern.compile("\\D");
    private final ChatService chatService;
    private final NotificationsService notifications;
    private String lastCopied = "";

    public void inspect(String text, String suspect) {
        if (suspect.isEmpty() || !text.contains(suspect)) {
            return;
        }
        Optional<String> found = findNumber(text);
        if (found.isEmpty()) {
            return;
        }
        String number = found.get();
        if (number.equals(lastCopied)) {
            return;
        }
        lastCopied = number;
        chatService.copyToClipboard(number);
        notifications.success("Скопирован анидеск: %s".formatted(number));
    }

    public void reset() {
        lastCopied = "";
    }

    private Optional<String> findNumber(String text) {
        Matcher matcher = CANDIDATE.matcher(text);
        String best = "";
        while (matcher.find()) {
            String digits = NON_DIGIT.matcher(matcher.group()).replaceAll("");
            if (isValidLength(digits) && digits.length() > best.length()) {
                best = digits;
            }
        }
        return best.isEmpty() ? Optional.empty() : Optional.of(best);
    }

    private boolean isValidLength(String digits) {
        return digits.length() >= MIN_LENGTH && digits.length() <= MAX_LENGTH;
    }
}