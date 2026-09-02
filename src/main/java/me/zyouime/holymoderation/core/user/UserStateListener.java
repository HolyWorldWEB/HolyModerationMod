package me.zyouime.holymoderation.core.user;

import java.util.Optional;

import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.LoggerService;
import me.zyouime.holymoderation.core.spy.ServerLocation;
import me.zyouime.holymoderation.core.spy.ServerType;
import me.zyouime.holymoderation.core.states.UserState;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;

import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public record UserStateListener(UserState userState, UserLocator userLocator, ChatService chatService, LoggerService logger) {

    public ActionResult onMessage(Text message) {
        String text = chatService.formatReceivedText(message.getString());
        if (text == null) {
            return ActionResult.PASS;
        }
        if (HolyWorldPatterns.isHubGate(text)) {
            enterHub();
            return ActionResult.PASS;
        } else if (HolyWorldPatterns.isLobbyNoCommand(text)) {
            enterHub();
            return ActionResult.FAIL;
        }
        if (userLocator.isSearching() && HolyWorldPatterns.isNoCommandOrAccess(text)) {
            userLocator.stop();
            return ActionResult.FAIL;
        }
        if (userState.hasLocation()) {
            return ActionResult.PASS;
        }
        if (!HolyWorldPatterns.isFindResponseAboutSelf(text, userState.getUserNickname())) {
            return ActionResult.PASS;
        }
        applyOwnLocation(text);
        return ActionResult.FAIL;
    }

    private void enterHub() {
        userState.setUserLocation(ServerLocation.of(ServerType.LOBBY));
        userLocator.stop();
    }

    private void applyOwnLocation(String text) {
        String raw = HolyWorldPatterns.parseFindStatus(text);
        if (raw == null) {
            logger.debug("Не удалось разобрать свой ответ /find: %s".formatted(text));
            return;
        }
        Optional<ServerLocation> parsed = ServerLocation.parse(raw);
        if (parsed.isEmpty()) {
            logger.debug("Неизвестная локация в /find: '%s'".formatted(raw));
            return;
        }
        userState.setUserLocation(parsed.get());
        userLocator.stop();
        logger.debug("Своя локация определена: %s".formatted(parsed.get().display()));
    }
}
