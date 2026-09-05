package me.zyouime.holymoderation.core.connection;

import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
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

    private static final Pattern HOLYWORLD = Pattern.compile("(?i).*holl?yworld.*");
    private final UserState userState;
    private ClientConnection connection;

    public void onGameJoin(ClientPlayNetworkHandler handler) {
        boolean sameConnection = handler.getConnection() == connection;
        connection = handler.getConnection();
        String address = resolveAddress(handler);
        boolean onHolyWorld = HOLYWORLD.matcher(address).matches();
        userState.setHacAlertsEnabled(false);
        userState.setConnected(true);
        userState.setOnHW(onHolyWorld);
        userState.setUserNickname(resolveNickname());
        applyLocation();
        if (sameConnection) {
            ServerEvents.SWITCH.invoker().onSwitch();
            return;
        }
        ServerEvents.JOIN.invoker().onJoin(address, onHolyWorld);
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
        return serverInfo.address;
    }

    private String resolveNickname() {
        ClientPlayerEntity player = MinecraftProvider.player();
        if (player == null) {
            return "";
        }
        return player.getGameProfile().name();
    }
}