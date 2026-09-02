package me.zyouime.holymoderation.core.api;

import me.zyouime.holymoderation.core.dto.*;
import me.zyouime.holymoderation.core.service.HttpClientService;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.parser.JsonParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record JournalApi(HttpClientService http, Supplier<String> tokenSupplier, LoggerService logger, JsonParser jsonParser) {

    private static final URI LINK = URI.create("https://journal.holyworld.me/srv/api/v1/");

    public CompletableFuture<JournalProfile> profile() {
        return get("me").thenApply(body -> jsonParser.parse(body, JournalProfile.class));
    }

    public CompletableFuture<JournalStats> stats() {
        return get("stats").thenApply(body -> jsonParser.parse(body, JournalStats.class));
    }

    public CompletableFuture<Boolean> hasActiveCheckout() {
        return get("checkout/status").thenApply(body -> jsonParser.parse(body, CheckoutStatus.class).status());
    }

    public CompletableFuture<Void> startCheckout(CheckoutRequest request) {
        return post("checkout/start", jsonParser.toJson(request)).thenAccept(this::checkoutHandler);
    }

    public CompletableFuture<Void> endCheckout(CheckoutEndRequest request) {
        return post("checkout/end", jsonParser.toJson(request)).thenAccept(this::checkoutHandler);
    }

    private CompletableFuture<String> get(String endpoint) {
        HttpRequest request = http.request(LINK.resolve(endpoint))
                .header("x-token", tokenSupplier.get())
                .GET()
                .build();
        return http.send(request).thenApply(HttpClientService::bodyOrThrow);
    }

    private CompletableFuture<HttpResponse<String>> post(String endpoint, String body) {
        HttpRequest request = http.request(LINK.resolve(endpoint))
                .header("x-token", tokenSupplier.get())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return http.send(request);
    }

    private void checkoutHandler(HttpResponse<String> response) {
        logger.debug(response.body());
    }
}