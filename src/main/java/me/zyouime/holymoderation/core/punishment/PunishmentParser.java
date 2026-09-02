package me.zyouime.holymoderation.core.punishment;

public final class PunishmentParser {

    private PunishmentParser() {
    }

    public static Result parse(PunishmentType type, String command) {
        String[] parts = command.split(" ", 3);
        if (parts.length < 2) {
            return Result.error("Вы не указали ник игрока и причину.");
        }
        String player = parts[1];
        if (parts.length < 3) {
            return Result.error(type.requiresTime() ? "Вы не указали время и причину." : "Вы не указали причину.");
        }
        String tail = parts[2];
        String time = null;
        String reason = tail;
        if (type.acceptsTime()) {
            String[] tailParts = tail.split(" ", 2);
            if (PunishmentTime.isValid(tailParts[0])) {
                time = tailParts[0];
                reason = tailParts.length > 1 ? tailParts[1] : "";
            }
        }
        if (type.requiresTime() && time == null) {
            return Result.error("Вы не указали время. Формат: 1-9999s, 1-9999m, 1-9999h или 1-9999d");
        }
        if (reason.isBlank()) {
            return Result.error("Вы не указали причину.");
        }
        return Result.of(new Punishment(type, player, time, reason));
    }

    public record Result(Punishment punishment, String error) {

        public static Result of(Punishment punishment) {
            return new Result(punishment, null);
        }

        public static Result error(String error) {
            return new Result(null, error);
        }
    }
}
