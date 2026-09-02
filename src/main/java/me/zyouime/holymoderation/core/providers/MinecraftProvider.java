package me.zyouime.holymoderation.core.providers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;

public final class MinecraftProvider {

    private static final String SPAWN_WORLD = "minecraft:spawn_world";

    public static boolean isSpawnWorld() {
        ClientWorld world = world();
        if (world == null) {
            return false;
        }
        return world.getRegistryKey().getValue().toString().equals(SPAWN_WORLD);
    }

    public static MinecraftClient client() {
        return MinecraftClient.getInstance();
    }

    public static ClientPlayerEntity player() {
        return client().player;
    }

    public static ClientWorld world() {
        return client().world;
    }

    public static Window window() {
        return client().getWindow();
    }

    public static Screen currentScreen() {
        return client().currentScreen;
    }

    public static ClientPlayNetworkHandler networkHandler() {
        return client().getNetworkHandler();
    }
}
