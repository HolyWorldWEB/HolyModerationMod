package me.zyouime.holymoderation.core.dto;

public record CheckoutEndRequest(
        String result,
        String banReason,
        boolean destroyStash) {}