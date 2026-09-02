package me.zyouime.holymoderation.core.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import java.util.Locale;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public final class Cmd {

    private static final SuggestionProvider<FabricClientCommandSource> ONLINE_PLAYERS = (context, builder) -> {
        ClientPlayNetworkHandler networkHandler = MinecraftProvider.networkHandler();
        if (networkHandler != null) {
            String typed = builder.getRemaining().toLowerCase(Locale.ROOT);
            networkHandler.getPlayerList().stream()
                    .map(entry -> entry.getProfile().name())
                    .filter(nick -> nick.toLowerCase(Locale.ROOT).startsWith(typed))
                    .forEach(builder::suggest);
        }
        return builder.buildFuture();
    };

    private Cmd() {
    }

    public static SimpleCommandExceptionType error(String message) {
        return new SimpleCommandExceptionType(Text.literal(message));
    }

    public static RequiredArgumentBuilder<FabricClientCommandSource, String> word(String name) {
        return ClientCommandManager.argument(name, StringArgumentType.word());
    }

    public static RequiredArgumentBuilder<FabricClientCommandSource, String> text(String name) {
        return ClientCommandManager.argument(name, StringArgumentType.greedyString());
    }

    public static RequiredArgumentBuilder<FabricClientCommandSource, Integer> number(String name, int min, int max) {
        return ClientCommandManager.argument(name, IntegerArgumentType.integer(min, max));
    }

    public static RequiredArgumentBuilder<FabricClientCommandSource, String> player(String name) {
        return word(name).suggests(ONLINE_PLAYERS);
    }

    public static RequiredArgumentBuilder<FabricClientCommandSource, String> choice(String name, List<String> values) {
        return word(name).suggests(fromValues(values));
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> sub(String name) {
        return ClientCommandManager.literal(name);
    }

    public static String str(CommandContext<FabricClientCommandSource> context, String name) {
        return StringArgumentType.getString(context, name);
    }

    public static int num(CommandContext<FabricClientCommandSource> context, String name) {
        return IntegerArgumentType.getInteger(context, name);
    }

    private static SuggestionProvider<FabricClientCommandSource> fromValues(List<String> values) {
        return (context, builder) -> {
            String typed = builder.getRemaining().toLowerCase(Locale.ROOT);
            values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(typed)).forEach(builder::suggest);
            return builder.buildFuture();
        };
    }
}
