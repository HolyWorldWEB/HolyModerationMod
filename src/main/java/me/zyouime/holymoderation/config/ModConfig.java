package me.zyouime.holymoderation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.zyouime.holymoderation.config.adapter.ColorAdapter;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ModConfig {

    public static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Color.class, new ColorAdapter()).create();

    public static File getConfigFile(String name) {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), name + ".json");
    }

    public static JsonObject loadConfig(File configFile, Gson gson) {
        if (!configFile.exists()) {
            return new JsonObject();
        }
        try (FileReader fileReader = new FileReader(configFile)) {
            JsonObject object = gson.fromJson(fileReader, JsonObject.class);
            if (object != null) {
                return object;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new JsonObject();
    }

    public static void saveConfig(JsonObject object, File configFile, Gson gson) {
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            gson.toJson(object, fileWriter);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}