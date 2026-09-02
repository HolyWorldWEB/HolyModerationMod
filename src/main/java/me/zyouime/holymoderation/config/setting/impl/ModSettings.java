package me.zyouime.holymoderation.config.setting.impl;

import me.zyouime.holymoderation.config.ModConfig;
import me.zyouime.holymoderation.config.setting.AbstractSettings;
import me.zyouime.holymoderation.config.setting.Setting;
import me.zyouime.holymoderation.config.setting.SettingsTypes;

import java.util.List;

public final class ModSettings extends AbstractSettings {

    public final Setting<String> apiToken = registerSetting(Setting.of("apiToken", SettingsTypes.STRING, ""));
    public final Setting<Boolean> copyButton = registerSetting(Setting.of("copyButton", SettingsTypes.BOOLEAN, false));
    public final Setting<String> copyButtonText = registerSetting(Setting.of("copyButtonText", SettingsTypes.STRING, "§f§l[§a§lcopy§f§l]"));
    public final Setting<String> playerMarker = registerSetting(Setting.of("playerMarker", SettingsTypes.STRING, "§d§l[CHECK]"));
    public final Setting<Boolean> autoVanish = registerSetting(Setting.of("autoVanish", SettingsTypes.BOOLEAN, true));
    public final Setting<Boolean> autoFly = registerSetting(Setting.of("autoFly", SettingsTypes.BOOLEAN, false));
    public final Setting<Boolean> autoGm3 = registerSetting(Setting.of("autoGm3", SettingsTypes.BOOLEAN, false));
    public final Setting<Boolean> autoHacAlerts = registerSetting(Setting.of("autoHacAlerts", SettingsTypes.BOOLEAN, false));
    public final Setting<Boolean> autoGod = registerSetting(Setting.of("autoGod", SettingsTypes.BOOLEAN, false));
    public final Setting<Boolean> dupeIp = registerSetting(Setting.of("dupeIp", SettingsTypes.BOOLEAN, false));
    public final Setting<Boolean> autoAnyDesk = registerSetting(Setting.of("autoAnyDesk", SettingsTypes.BOOLEAN, true));
    public final Setting<Boolean> autoCheckoutTp = registerSetting(Setting.of("autoCheckoutTp", SettingsTypes.BOOLEAN, true));
//    public final Setting<Boolean> autoBan = registerSetting(Setting.of("autoBan", SettingsTypes.BOOLEAN, true));
    public final Setting<Integer> spyDelay = registerSetting(Setting.of("spyDelay", SettingsTypes.INTEGER, 2));
    public final Setting<Boolean> autoSpyTp = registerSetting(Setting.of("autoSpyTp", SettingsTypes.BOOLEAN, false));
    public final Setting<String> vkLink = registerSetting(Setting.of("vkLink", SettingsTypes.STRING, ""));
    public final Setting<Boolean> spyHudEnabled = registerSetting(Setting.of("spyHudEnabled", SettingsTypes.BOOLEAN, true));
    public final Setting<Integer> spyHudScale = registerSetting(Setting.of("spyHudScale", SettingsTypes.INTEGER, 100));
    public final Setting<Float> spyHudX = registerSetting(Setting.of("spyHudX", SettingsTypes.FLOAT, 0.012f));
    public final Setting<Float> spyHudY = registerSetting(Setting.of("spyHudY", SettingsTypes.FLOAT, 0.34f));
    public final Setting<Integer> notificationScale = registerSetting(Setting.of("notificationScale", SettingsTypes.INTEGER, 100));
    public final Setting<Boolean> checkoutHudEnabled = registerSetting(Setting.of("checkoutHudEnabled", SettingsTypes.BOOLEAN, true));
    public final Setting<Integer> checkoutHudScale = registerSetting(Setting.of("checkoutHudScale", SettingsTypes.INTEGER, 100));
    public final Setting<Float> checkoutHudX = registerSetting(Setting.of("checkoutHudX", SettingsTypes.FLOAT, 0.0f));
    public final Setting<Float> checkoutHudY = registerSetting(Setting.of("checkoutHudY", SettingsTypes.FLOAT, 0.18f));
    public final Setting<Boolean> obsEnabled = registerSetting(Setting.of("obsEnabled", SettingsTypes.BOOLEAN, false));
    public final Setting<String> obsHost = registerSetting(Setting.of("obsHost", SettingsTypes.STRING, "localhost"));
    public final Setting<String> obsPort = registerSetting(Setting.of("obsPort", SettingsTypes.STRING, "4455"));
    public final Setting<String> obsPassword = registerSetting(Setting.of("obsPassword", SettingsTypes.STRING, ""));
    public final Setting<List<String>> checkoutTexts = registerSetting(Setting.of("checkoutTexts", SettingsTypes.LIST_STRING, List.of()));
    public ModSettings() {
        super(ModConfig.getConfigFile("holymoderation"), ModConfig.DEFAULT_GSON);
    }
}
