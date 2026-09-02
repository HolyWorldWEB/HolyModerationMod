package me.zyouime.holymoderation.core.dto;

public record JournalStats(
        int mutesAll,
        int gaurantsAll,
        int mutesMonth,
        int gaurantsMonth,
        int mutesToday,
        int bansToday,
        int gaurantsToday,
        int netvisionSpecBansToday,
        int netvisionSpecBansRevisesEquivalent) {}