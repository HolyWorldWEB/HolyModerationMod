package me.zyouime.holymoderation.core.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.command.Cmd;
import me.zyouime.holymoderation.core.command.Exec;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.settings.SettingsCatalog;
import me.zyouime.holymoderation.core.util.Colors;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public record TextsCommand(ModSettings settings, ChatService chatService, NotificationsService notifications) implements ModCommand {

    private static final int MAX_TEXTS = 20;
    private static final String NUMBER_ARG = "номер";
    private static final String TEXT_ARG = "текст";
    private static final SimpleCommandExceptionType TOO_MANY = Cmd.error("Больше %d текстов добавить нельзя.".formatted(MAX_TEXTS));
    private static final SimpleCommandExceptionType NO_SUCH_TEXT = Cmd.error("Текста с таким номером нет.");
    private static final SimpleCommandExceptionType TOO_LONG = Cmd.error("Текст слишком длинный, максимум %d символов.".formatted(SettingsCatalog.MAX_TEXT_LENGTH));

    @Override
    public String name() {
        return "texts";
    }

    @Override
    public void configure(LiteralArgumentBuilder<FabricClientCommandSource> node, Exec exec) {
        node.executes(exec.of(context -> list()))
                .then(Cmd.sub("add")
                        .then(Cmd.text(TEXT_ARG)
                                .executes(exec.of(context -> add(Cmd.str(context, TEXT_ARG))))))
                .then(Cmd.sub("edit")
                        .then(Cmd.number(NUMBER_ARG, 1, MAX_TEXTS)
                                .then(Cmd.text(TEXT_ARG)
                                        .executes(exec.of(context ->
                                                edit(Cmd.num(context, NUMBER_ARG), Cmd.str(context, TEXT_ARG)))))))
                .then(Cmd.sub("remove")
                        .then(Cmd.number(NUMBER_ARG, 1, MAX_TEXTS)
                                .executes(exec.of(context -> remove(Cmd.num(context, NUMBER_ARG))))))
                .then(Cmd.sub("list")
                        .executes(exec.of(context -> list())))
                .then(Cmd.sub("clear")
                        .executes(exec.of(context -> clear())));
    }

    private void add(String text) throws CommandSyntaxException {
        List<String> texts = new ArrayList<>(settings.checkoutTexts.getValue());
        if (texts.size() >= MAX_TEXTS) {
            throw TOO_MANY.create();
        }
        texts.add(format(text));
        save(texts);
        notifications.success("Текст добавлен под номером %d.".formatted(texts.size()));
    }

    private void edit(int number, String text) throws CommandSyntaxException {
        List<String> texts = new ArrayList<>(settings.checkoutTexts.getValue());
        if (number > texts.size()) {
            throw NO_SUCH_TEXT.create();
        }
        texts.set(number - 1, format(text));
        save(texts);
        notifications.success("Текст номер %d изменён.".formatted(number));
    }

    private void remove(int number) throws CommandSyntaxException {
        List<String> texts = new ArrayList<>(settings.checkoutTexts.getValue());
        if (number > texts.size()) {
            throw NO_SUCH_TEXT.create();
        }
        String removed = texts.remove(number - 1);
        save(texts);
        notifications.success("Текст удалён: %s".formatted(removed));
    }

    private void clear() {
        save(List.of());
        notifications.success("Все тексты удалены.");
    }

    private void list() {
        List<String> texts = settings.checkoutTexts.getValue();
        if (texts.isEmpty()) {
            chatService.clientMessage(Text.of("Текстов нет. Добавить: /hm texts add <текст>"));
            return;
        }
        chatService.clientMessage(Text.of("%s%sЗаготовленные тексты:".formatted(Colors.AQUA, Colors.BOLD)));
        for (int index = 0; index < texts.size(); index++) {
            chatService.clientMessage(chatService.suggestTextComponent("%s%d.%s %s".formatted(Colors.GOLD, index + 1, Colors.WHITE, texts.get(index)), "Нажмите, чтобы подставить команду изменения", "/hm texts edit %d ".formatted(index + 1)));
        }
    }

    private static String format(String text) throws CommandSyntaxException {
        String formatted = text.replace('&', '§');
        if (formatted.length() > SettingsCatalog.MAX_TEXT_LENGTH) {
            throw TOO_LONG.create();
        }
        return formatted;
    }

    private void save(List<String> texts) {
        settings.checkoutTexts.setValue(List.copyOf(texts));
        settings.saveSettings();
    }
}
