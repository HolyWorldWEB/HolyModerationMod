package me.zyouime.holymoderation.core.connection;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.ModuleManager;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.spy.ServerLocation;
import me.zyouime.holymoderation.core.spy.ServerType;
import me.zyouime.holymoderation.core.states.UserState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.world.GameMode;

@RequiredArgsConstructor
public final class ConnectionTracker {

    private final UserState userState;
    private ClientConnection connection;
    private final ModuleManager manager;
    private final NotificationsService notificationsService;
    private static final Set<String> HW_HOST_SUFFIXES = Set.of(
            "holyworld.me",
            "holyworld.ru",
            "holyworld.io");


    public void onGameJoin(ClientPlayNetworkHandler handler) {
        boolean sameConnection = handler.getConnection() == connection;
        connection = handler.getConnection();
        String address = resolveAddress(handler);
        boolean onHolyWorld = isHolyWorldAddress(address);
        manager.toggle(onHolyWorld);
        if (!onHolyWorld) {
            notificationsService.warning("Вы находитесь не на HolyWorld. Мод будет выключен");
            userState.reset();
            return;
        }
        userState.setHacAlertsEnabled(false);
        userState.setConnected(true);
        userState.setOnHW(true);
        userState.setUserNickname(resolveNickname());
        applyLocation();
        if (sameConnection) {
            ServerEvents.SWITCH.invoker().onSwitch();
            return;
        }
        ServerEvents.JOIN.invoker().onJoin(address);
    }

    public void onDisconnect() {
        if (connection == null) {
            return;
        }
        connection = null;
        ServerEvents.LEAVE.invoker().onLeave();
        userState.reset();
    }

    public boolean isConnected() {
        return connection != null;
    }

    private void applyLocation() {
        if (isHub()) {
            userState.setUserLocation(ServerLocation.of(ServerType.LOBBY));
            return;
        }
        userState.setUserLocation(null);
    }

    private boolean isHub() {
        ClientPlayerInteractionManager interactionManager = MinecraftProvider.client().interactionManager;
        if (interactionManager == null) {
            return false;
        }
        return interactionManager.getCurrentGameMode() == GameMode.ADVENTURE;
    }

    private String resolveAddress(ClientPlayNetworkHandler handler) {
        ServerInfo serverInfo = handler.getServerInfo();
        if (serverInfo == null) {
            return "";
        }
        return serverInfo.address.toLowerCase();
    }

    private String resolveNickname() {
        ClientPlayerEntity player = MinecraftProvider.player();
        if (player == null) {
            return "";
        }
        return player.getGameProfile().name();
    }

    private static boolean isHolyWorldAddress(String address) {
        try {
            String host = address.split(":")[0].toLowerCase(Locale.ROOT);
            return HW_HOST_SUFFIXES.stream().anyMatch(host::endsWith);
        } catch (Exception e) {
            return false;
        }
    }
}