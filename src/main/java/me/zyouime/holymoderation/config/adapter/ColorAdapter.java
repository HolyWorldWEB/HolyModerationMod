package me.zyouime.holymoderation.config.adapter;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {
    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        return new Color(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt(), jsonArray.get(2).getAsInt(), jsonArray.get(3).getAsInt());
    }

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(src.getRed());
        jsonArray.add(src.getGreen());
        jsonArray.add(src.getBlue());
        jsonArray.add(src.getAlpha());
        return jsonArray;
    }
}