package me.zyouime.holymoderation.core.service;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public final class HttpClientService {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public CompletableFuture<HttpResponse<String>> send(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    public CompletableFuture<Path> download(URI uri, Path target) {
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofMinutes(2)).GET().build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(target)).thenApply(HttpResponse::body);
    }

    public HttpRequest.Builder request(URI uri) {
        //        defaultHeaders.forEach(builder::header);
        return HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(15));
    }

    public static String bodyOrThrow(HttpResponse<String> response) {
        int code = response.statusCode();
        if (code >= 200 && code < 300) {
            return response.body();
        }
        if (code == 401 || code == 403) {
            throw new RuntimeException("Неверный токен");
        }
        throw new RuntimeException("Сервер ответил кодом " + code);
    }
}