package me.zyouime.holymoderation.core.checkout;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CheckoutReason {

    REPORT("report", "по репорту"),
    CHECKOUT("checkout", "обычная"),
    AUTOBUY("autobuy", "автобаер"),
    AUTOSELL("autosell", "автоселлер"),
    CUSTOMKA("customka", "кастомка"),
    PERSONAL("personal", "личная"),
    TO_MANY_CHECKS("toManyChecks", "много проверок"),
    CANDIDATE("candidate", "кандидат");

    private final String apiValue;
    private final String label;

    public static Optional<CheckoutReason> byApiValue(String value) {
        return Arrays.stream(values())
                .filter(reason -> reason.apiValue.equalsIgnoreCase(value))
                .findFirst();
    }

    public static List<String> apiValues() {
        return Arrays.stream(values()).map(CheckoutReason::getApiValue).toList();
    }
}
