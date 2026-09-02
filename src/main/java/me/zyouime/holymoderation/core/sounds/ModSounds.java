package me.zyouime.holymoderation.core.sounds;

import me.zyouime.holymoderation.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static SoundEvent CLICK = registerSound("click");
    public static SoundEvent ON = registerSound("on");
    public static SoundEvent OFF = registerSound("off");
    public static SoundEvent GUI_OPEN = registerSound("gui_open");

    public static void init() {}

    public static SoundEvent registerSound(String sound) {
        Identifier id = Identifier.of(Main.MOD_ID, sound);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void playClick() {
        playSound(CLICK, 1.0f, 0.35f);
    }

    public static void playClick(float pitch) {
        playSound(CLICK, pitch, 0.35f);
    }

    public static void playSound(SoundEvent event) {
        playSound(event,1f, 1f);
    }

    public static void playSound(SoundEvent event, float pitch, float volume) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getSoundManager() != null) {
            client.getSoundManager().play(PositionedSoundInstance.ui(event, pitch, volume));
        }
    }
}