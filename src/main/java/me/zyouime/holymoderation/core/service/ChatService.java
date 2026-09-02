package me.zyouime.holymoderation.core.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import me.zyouime.holymoderation.core.util.HolyWorldPatterns;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.regex.Pattern;

import static me.zyouime.holymoderation.core.util.Colors.*;

@RequiredArgsConstructor
public final class ChatService {

    private static final Pattern COLOR_CODE = Pattern.compile("§[0-9a-zA-Z]");
    public final Text HMTextComponent = Text.of("%s%s[%s%sHM%s%s]%s ".formatted(BLUE, BOLD, DARK_AQUA, BOLD, BLUE, BOLD, WHITE));
    private final ModSettings modSettings;
    @Getter
    private boolean internalCommand;

    public void chatMessage(String message) {
        ClientPlayNetworkHandler networkHandler = MinecraftProvider.networkHandler();
        if (networkHandler != null) {
            if (message.startsWith("/")) {
                internalCommand = true;
                networkHandler.sendChatCommand(message.substring(1));
                internalCommand = false;
            } else {
                networkHandler.sendChatMessage(message);
            }
        }
    }

    public void clientMessage(Text text) {
        ClientPlayerEntity player = MinecraftProvider.player();
        if (player != null) {
            player.sendMessage(generateComponent(HMTextComponent, text), false);
        }
    }

    public static String stripColor(String text) {
        return COLOR_CODE.matcher(text).replaceAll(StringUtils.EMPTY);
    }

    public String formatReceivedText(String text) {
        text = stripColor(text);
        for (String ignoredString : HolyWorldPatterns.IGNORED_PREFIXES) {
            if (text.startsWith(ignoredString)) return null;
        }
        text = text.replace(stripColor(modSettings.copyButtonText.getValue()), StringUtils.EMPTY);
        return text;
    }

    public String formatLocation(String location) {
        return HolyWorldPatterns.formatLocation(location);
    }

    public void copyToClipboard(String text) {
        MinecraftProvider.client().keyboard.setClipboard(text);
    }

    public MutableText suggestTextComponent(String componentText) {
        Text suggestComponent = Text.of(componentText);
        return suggestComponent.copy().setStyle(
                suggestComponent.getStyle()
                        .withHoverEvent(new HoverEvent.ShowText(Text.of("Нажмите, чтобы подставить команду.")))
                        .withClickEvent(new ClickEvent.SuggestCommand(stripColor(componentText))));
    }

    public MutableText suggestTextComponent(String componentText, String hint, String toSuggestText) {
        Text suggestComponent = Text.of(componentText);
        return suggestComponent.copy().setStyle(
                suggestComponent.getStyle()
                        .withHoverEvent(new HoverEvent.ShowText(Text.of(hint)))
                        .withClickEvent(new ClickEvent.SuggestCommand(toSuggestText)));
    }

    public MutableText hoverTextComponent(String componentText, String hint) {
        Text hoverComponent = Text.of(componentText);
        return hoverComponent.copy().setStyle(hoverComponent.getStyle().withHoverEvent(new HoverEvent.ShowText(Text.of(hint))));
    }

    public MutableText copyTextComponent(String componentText, String hint, String toCopyText) {
        Text copyComponent = Text.of(componentText);
        return copyComponent.copy().setStyle(
                copyComponent.getStyle()
                        .withHoverEvent(new HoverEvent.ShowText(Text.of(hint)))
                        .withClickEvent(new ClickEvent.CopyToClipboard(toCopyText)));
    }

    public MutableText openURLTextComponent(String componentText, String hint, String url) {
        Text openURLComponent = Text.of(componentText);
        return openURLComponent.copy().setStyle(
                openURLComponent.getStyle()
                        .withHoverEvent(new HoverEvent.ShowText(Text.of(hint)))
                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(url))));
    }

    public MutableText copyTextComponent(Text component, String hint, String toCopyText) {
        return component.copy().setStyle(
                component.getStyle()
                        .withHoverEvent(new HoverEvent.ShowText(Text.of(hint)))
                        .withClickEvent(new ClickEvent.CopyToClipboard(toCopyText)));
    }

    public MutableText generateComponent(Text... components) {
        MutableText newComponent = Text.empty();
        Arrays.asList(components).forEach(newComponent::append);
        return newComponent;
    }
}