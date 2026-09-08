package me.zyouime.holymoderation.core.dto;

public record JournalStats(
        ChecksInfo revisesAll,
        ChecksInfo revisesWeek,
        ChecksInfo revisesToday,
        ChecksInfo revisesMonth,
        int mutesAll,
        int gaurantsAll,
        int mutesMonth,
        int gaurantsMonth,
        int mutesToday,
        int bansToday,
        int gaurantsToday,
        int netvisionSpecBansToday,
        int netvisionSpecBansRevisesEquivalent) {}