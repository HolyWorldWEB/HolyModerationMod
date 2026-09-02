package me.zyouime.holymoderation;

import lombok.Getter;
import me.zyouime.holymoderation.core.context.ModContext;
import me.zyouime.holymoderation.core.sounds.ModSounds;
import me.zyouime.holymoderation.gui.AbstractScreen;
import me.zyouime.holymoderation.gui.SettingsScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Main implements ModInitializer {

    public static final String MOD_ID = "holymoderation";
    @Getter private static ModContext modContext;
    private final KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, MOD_ID));
    private final KeyBinding settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Открыть настройки",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            category));

    @Override
    public void onInitialize() {
        modContext = ModContext.createContext();
        ModSounds.init();
        this.registerEvents();
    }

    private void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (settingsKey.wasPressed()) {
                client.setScreen(new SettingsScreen(client.currentScreen, modContext.settings()));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((client, connection) -> modContext.connectionTracker().onDisconnect());
    }

}
