package me.zyouime.holymoderation.config.setting;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.function.Consumer;

@Getter
public class Setting<T> {

    private T value;
    private final T defaultValue;
    private final String configKey;
    private final Type type;
    @Setter private Consumer<T> setCallback;

    private Setting(String configKey, Type type, T defaultValue, Consumer<T> setCallback) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
        this.type = type;
        this.setCallback = setCallback;
    }

    public static <W> Setting<W> of(String configKey, Type type, W defaultValue, Consumer<W> setCallback) {
        return new Setting<>(configKey, type, defaultValue, setCallback);
    }

    public static <W> Setting<W> of(String configKey, Type type, W defaultValue) {
       return new Setting<>(configKey, type, defaultValue, null);
    }

    public void initValue(JsonElement value, Gson gson) {
        this.value = gson.fromJson(value, this.type);
    }

    public void save(Gson gson, JsonObject config) {
        config.add(configKey, gson.toJsonTree(this.value, this.type));
    }

    public void setValue(T value) {
        this.value = value;
        if (setCallback != null) {
            setCallback.accept(value);
        }
    }

    public void reset() {
        this.setValue(defaultValue);
    }
}