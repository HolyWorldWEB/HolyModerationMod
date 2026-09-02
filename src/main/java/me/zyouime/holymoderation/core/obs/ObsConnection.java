package me.zyouime.holymoderation.core.obs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.zyouime.holymoderation.core.service.LoggerService;

public final class ObsConnection implements WebSocket.Listener {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final String REQUEST_ID = "requestId";
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
    private final Map<String, CompletableFuture<JsonObject>> pending = new ConcurrentHashMap<>();
    private final StringBuilder incoming = new StringBuilder();
    private final LoggerService logger;
    private final Consumer<JsonObject> eventHandler;
    private volatile WebSocket socket;
    private volatile CompletableFuture<Void> handshake = CompletableFuture.completedFuture(null);
    private volatile String password = "";

    public ObsConnection(LoggerService logger, Consumer<JsonObject> eventHandler) {
        this.logger = logger;
        this.eventHandler = eventHandler;
    }

    public boolean isConnected() {
        WebSocket current = socket;
        return current != null && !current.isInputClosed();
    }

    public CompletableFuture<Void> connect(String host, int port, String password) {
        if (isConnected()) {
            return handshake;
        }
        this.password = password;
        CompletableFuture<Void> ready = new CompletableFuture<>();
        handshake = ready;
        http.newWebSocketBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .buildAsync(URI.create("ws://%s:%d".formatted(host, port)), this)
                .whenComplete((webSocket, error) -> {
                    if (error != null) {
                        ready.completeExceptionally(error);
                        return;
                    }
                    socket = webSocket;
                });
        return ready;
    }

    public void disconnect() {
        WebSocket current = socket;
        socket = null;
        failPending(new IllegalStateException("Соединение с OBS закрыто"));
        if (current != null) {
            current.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
        }
    }

    public CompletableFuture<JsonObject> request(String type, JsonObject data) {
        WebSocket current = socket;
        if (current == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Нет соединения с OBS"));
        }
        String id = UUID.randomUUID().toString();
        JsonObject payload = new JsonObject();
        payload.addProperty("requestType", type);
        payload.addProperty(REQUEST_ID, id);
        if (data != null) {
            payload.add("requestData", data);
        }
        CompletableFuture<JsonObject> answer = new CompletableFuture<>();
        pending.put(id, answer);
        send(current, ObsMessage.REQUEST, payload);
        return answer;
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        incoming.append(data);
        if (last) {
            String message = incoming.toString();
            incoming.setLength(0);
            try {
                handle(JsonParser.parseString(message).getAsJsonObject());
            } catch (Exception e) {
                logger.exception("OBS: не удалось разобрать сообщение: " + e);
            }
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        logger.exception("OBS: ошибка соединения: " + error);
        socket = null;
        handshake.completeExceptionally(error);
        failPending(error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        socket = null;
        failPending(new IllegalStateException("Соединение с OBS закрыто"));
        return null;
    }

    private void send(WebSocket target, int opCode, JsonObject payload) {
        JsonObject frame = new JsonObject();
        frame.addProperty("op", opCode);
        frame.add("d", payload);
        target.sendText(frame.toString(), true);
    }

    private void handle(JsonObject frame) {
        int opCode = frame.get("op").getAsInt();
        JsonObject data = frame.getAsJsonObject("d");
        switch (opCode) {
            case ObsMessage.HELLO -> identify(data);
            case ObsMessage.IDENTIFIED -> handshake.complete(null);
            case ObsMessage.REQUEST_RESPONSE -> completeRequest(data);
            case ObsMessage.EVENT -> eventHandler.accept(data);
            default -> logger.debug("OBS: неизвестный опкод " + opCode);
        }
    }

    private void identify(JsonObject hello) {
        JsonObject payload = new JsonObject();
        payload.addProperty("rpcVersion", ObsMessage.RPC_VERSION);
        if (hello.has("authentication")) {
            JsonObject auth = hello.getAsJsonObject("authentication");
            payload.addProperty("authentication", ObsAuth.response(password, auth.get("salt").getAsString(), auth.get("challenge").getAsString()));
        }
        send(socket, ObsMessage.IDENTIFY, payload);
    }

    private void completeRequest(JsonObject data) {
        CompletableFuture<JsonObject> answer = pending.remove(data.get(REQUEST_ID).getAsString());
        if (answer == null) {
            return;
        }
        JsonObject status = data.getAsJsonObject("requestStatus");
        if (status.get("result").getAsBoolean()) {
            answer.complete(data.has("responseData") ? data.getAsJsonObject("responseData") : new JsonObject());
            return;
        }
        String comment = status.has("comment") ? status.get("comment").getAsString() : "неизвестная ошибка";
        answer.completeExceptionally(new IllegalStateException("OBS отказал: " + comment));
    }

    private void failPending(Throwable error) {
        pending.values().forEach(future -> future.completeExceptionally(error));
        pending.clear();
    }
}