package me.zyouime.holymoderation.core.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public final class JsonParser {

    private static final Gson GSON = new GsonBuilder().create();

    public <T> T parse(String body, Class<T> type) {
        try {
            T result = GSON.fromJson(body, type);
            if (result == null) {
                throw new RuntimeException("Пустой ответ сервера");
            }
            return result;
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Некорректный json в ответе", e);
        }
    }

    public String toJson(Object value) {
        return GSON.toJson(value);
    }
}