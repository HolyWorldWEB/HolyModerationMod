package me.zyouime.holymoderation.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.gui.SettingsScreen;

public final class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new SettingsScreen(parent, Main.getModContext().settings());
    }
}
