package me.zyouime.holymoderation.core.punishment;

public final class PunishmentTime {
    private static final int MAX_DIGITS = 5;
    private static final String UNIT_PATTERN = "(?i)[smhd]";

    private PunishmentTime() {
    }

    public static boolean isValid(String time) {
        if (time == null || time.length() < 2) {
            return false;
        }
        String unit = time.substring(time.length() - 1);
        if (!unit.matches(UNIT_PATTERN)) {
            return false;
        }
        String amount = time.substring(0, time.length() - 1);
        if (amount.length() > MAX_DIGITS) {
            return false;
        }
        return isPositiveNumber(amount);
    }

    public static boolean looksLikeNumber(String value) {
        if (value == null || value.length() < 2) {
            return false;
        }
        String unit = value.substring(value.length() - 1);
        if (unit.matches(UNIT_PATTERN)) {
            return false;
        }
        return isPositiveNumber(value.substring(0, value.length() - 1));
    }

    private static boolean isPositiveNumber(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (char symbol : value.toCharArray()) {
            if (!Character.isDigit(symbol)) {
                return false;
            }
        }
        return true;
    }
}
