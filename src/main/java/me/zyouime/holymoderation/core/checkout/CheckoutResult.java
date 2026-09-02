package me.zyouime.holymoderation.core.checkout;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CheckoutResult {

    CLEAN("clean", "чистый", false, false),
    BAN("ban", "бан", true, true),
    AUTOBUY("autobuy", "автобай", false, true),
    AUTOSELL("autosell", "автоселл", false, true);

    private final String apiValue;
    private final String label;
    private final boolean needsBanReason;
    private final boolean needsStashChoice;

    public static Optional<CheckoutResult> byApiValue(String value) {
        return Arrays.stream(values())
                .filter(result -> result.apiValue.equalsIgnoreCase(value))
                .findFirst();
    }

    public static List<String> apiValues() {
        return Arrays.stream(values()).map(CheckoutResult::getApiValue).toList();
    }
}
