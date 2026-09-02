package me.zyouime.holymoderation.core.dto;

public record CheckoutRequest(
        String username,
        String reason,
        String mode,
        int anarchyNumber,
        boolean isPvpAnarchy) {}