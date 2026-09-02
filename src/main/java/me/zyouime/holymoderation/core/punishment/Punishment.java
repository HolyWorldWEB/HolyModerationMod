package me.zyouime.holymoderation.core.punishment;

public record Punishment(PunishmentType type, String player, String time, String reason) {

    public boolean isPermanent() {
        return time == null;
    }
}
