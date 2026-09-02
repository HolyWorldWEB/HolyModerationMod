package me.zyouime.holymoderation.core.punishment;

import java.util.Optional;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.dto.JournalProfile;
import me.zyouime.holymoderation.core.states.ModeratorState;

public record VkLinkProvider(ModeratorState moderatorState, ModSettings settings) {

    private static final String LINK_FORMAT = "https://vk.com/id%d";

    public Optional<String> link() {
        JournalProfile profile = moderatorState.getProfile();
        if (profile != null && profile.idVk() > 0) {
            return Optional.of(LINK_FORMAT.formatted(profile.idVk()));
        }
        String manual = settings.vkLink.getValue();
        if (manual.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(manual);
    }

    public boolean isFallback() {
        JournalProfile profile = moderatorState.getProfile();
        return profile == null || profile.idVk() <= 0;
    }
}
