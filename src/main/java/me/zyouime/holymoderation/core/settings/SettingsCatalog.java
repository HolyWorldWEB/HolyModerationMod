package me.zyouime.holymoderation.core.settings;

import java.util.List;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.settings.entry.NumberEntry;
import me.zyouime.holymoderation.core.settings.entry.SettingEntry;
import me.zyouime.holymoderation.core.settings.entry.TextEntry;
import me.zyouime.holymoderation.core.settings.entry.ToggleEntry;

public final class SettingsCatalog {

    public static final int MAX_TEXT_LENGTH = 200;
    public static final int SPY_DELAY_MIN = 0;
    public static final int SPY_DELAY_MAX = 60;
    public static final int NOTIFICATION_SCALE_MIN = 60;
    public static final int NOTIFICATION_SCALE_MAX = 180;
    public static final int HUD_SCALE_MIN = 60;
    public static final int HUD_SCALE_MAX = 180;

    private SettingsCatalog() {
    }

    public static List<SettingEntry<?>> of(ModSettings settings) {
        return List.of(
                new ToggleEntry("autovanish", "Автоматический ваниш", Agreement.MASCULINE, settings.autoVanish),
                new ToggleEntry("autofly", "Автоматический флай", Agreement.MASCULINE, settings.autoFly),
                new ToggleEntry("autogm3", "Автоматический гм3", Agreement.MASCULINE, settings.autoGm3),
                new ToggleEntry("autogod", "Автоматический god", Agreement.MASCULINE, settings.autoGod),
                new ToggleEntry("autoha", "Автоматические hac alerts", Agreement.PLURAL, settings.autoHacAlerts),
                new ToggleEntry("autodupeip", "Автоматический /dupeip", Agreement.MASCULINE, settings.dupeIp),
                new ToggleEntry("autotp", "Автоматический телепорт на /warp logo", Agreement.MASCULINE, settings.autoCheckoutTp),
                new ToggleEntry("autospytp", "Автоматический телепорт при слежке", Agreement.MASCULINE, settings.autoSpyTp),
//                new ToggleEntry("autoban", "Автоматический бан после проверки", Agreement.MASCULINE, settings.autoBan),
                new ToggleEntry("autocopy", "Автоматическое копирование айди AnyDesk", Agreement.NEUTER, settings.autoAnyDesk),
                new ToggleEntry("copy", "Кнопка копирования", Agreement.FEMININE, settings.copyButton),
                new TextEntry("copytext", "Текст кнопки копирования", settings.copyButtonText, MAX_TEXT_LENGTH, false),
                new TextEntry("marker", "Метка игрока на проверке", settings.playerMarker, MAX_TEXT_LENGTH, false),
                new TextEntry("apitoken", "Токен журнала", settings.apiToken, MAX_TEXT_LENGTH, false),
                new ToggleEntry("checkouthud", "Плашка проверки", Agreement.FEMININE, settings.checkoutHudEnabled),
                new ToggleEntry("obs", "Запись проверок в OBS", Agreement.FEMININE, settings.obsEnabled),
                new TextEntry("obshost", "Адрес OBS", settings.obsHost, MAX_TEXT_LENGTH, false),
                new TextEntry("obsport", "Порт OBS", settings.obsPort, 6, false),
                new TextEntry("obspassword", "Пароль OBS", settings.obsPassword, MAX_TEXT_LENGTH, true),
                new NumberEntry("checkouthudscale", "Масштаб плашки проверки", settings.checkoutHudScale, HUD_SCALE_MIN, HUD_SCALE_MAX),
                new NumberEntry("spydelay", "Задержка обновления слежки", settings.spyDelay, SPY_DELAY_MIN, SPY_DELAY_MAX));

    }
}
