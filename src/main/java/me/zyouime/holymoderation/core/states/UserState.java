package me.zyouime.holymoderation.core.states;

import me.zyouime.holymoderation.core.spy.ServerLocation;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserState extends AbstractState {

    private String userNickname = StringUtils.EMPTY;
    private ServerLocation userLocation = null;
    private boolean connected = false;
    private boolean onHW = false;
    private boolean gameInitCompleted = false;
    private boolean vanishEnabled = false;
    private boolean flyEnabled = false;
    private boolean gm3Enabled = false;
    private boolean hacAlertsEnabled = false;
    private boolean godEnabled = false;

    public boolean hasLocation() {
        return userLocation != null;
    }

    public boolean isInHub() {
        return userLocation != null && userLocation.isLobby();
    }

    @Override
    public void reset() {
        this.userNickname = StringUtils.EMPTY;
        this.userLocation = null;
        this.connected = false;
        this.onHW = false;
        this.gameInitCompleted = false;
        this.vanishEnabled = false;
        this.flyEnabled = false;
        this.gm3Enabled = false;
        this.hacAlertsEnabled = false;
        this.godEnabled = false;
    }
}
