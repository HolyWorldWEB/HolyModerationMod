package me.zyouime.holymoderation.core.dto;

public record JournalProfile(
        int id,
        String nickname,
        long idVk,
        String fullname,
        int neponyatki,
        int rank,
        int reprimands,
        int warns,
        String anarchyMode,
        int anarchy) {}