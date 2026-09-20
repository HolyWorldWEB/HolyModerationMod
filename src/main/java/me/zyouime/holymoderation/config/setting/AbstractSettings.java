package me.zyouime.holymoderation.config.setting;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.config.ModConfig;
import me.zyouime.holymoderation.core.service.LoggerService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractSettings {

    private final List<Setting<?>> settings = new ArrayList<>();
    private final File configFile;
    private final Gson gson;
    private final LoggerService logger;

    public <T extends Setting<?>> T registerSetting(T setting) {
        this.settings.add(setting);
        return setting;
    }

    public JsonObject loadConfig() {
        return ModConfig.loadConfig(configFile, gson);
    }

    public void loadSettings() {
        if (settings.isEmpty()) {
            return;
        }
        JsonObject config = loadConfig();
        boolean changed = false;
        for (Setting<?> setting : settings) {
            String key = setting.getConfigKey();
            try {
                if (!config.has(key) || config.get(key).isJsonNull()) {
                    config.add(key, gson.toJsonTree(setting.getDefaultValue()));
                    changed = true;
                }
                setting.initValue(config.get(key).deepCopy(), gson);
            } catch (Exception e) {
                logger.exception("Неккоректное значение настройки '%s'. Использовано значение по умолчанию".formatted(key));
                config.add(key, gson.toJsonTree(setting.getDefaultValue()));
                setting.initValue(gson.toJsonTree(setting.getDefaultValue()), gson);
                changed = true;
            }
        }
        if (changed) {
            ModConfig.saveConfig(config, configFile, gson);
        }
    }


    public void saveSettings() {
        JsonObject config = loadConfig();
        for (Setting<?> setting : settings) {
            setting.save(gson, config);
        }
        ModConfig.saveConfig(config, configFile, gson);
    }
}