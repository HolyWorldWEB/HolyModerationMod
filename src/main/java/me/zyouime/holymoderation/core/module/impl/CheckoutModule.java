package me.zyouime.holymoderation.core.module.impl;

import java.util.List;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.checkout.AnyDeskExtractor;
import me.zyouime.holymoderation.core.checkout.BanReasonLookup;
import me.zyouime.holymoderation.core.checkout.CheckoutChatListener;
import me.zyouime.holymoderation.core.checkout.CheckoutJournal;
import me.zyouime.holymoderation.core.checkout.CheckoutPrompts;
import me.zyouime.holymoderation.core.command.ModCommand;
import me.zyouime.holymoderation.core.command.impl.*;
import me.zyouime.holymoderation.core.fabric.events.chat.MessageEvent;
import me.zyouime.holymoderation.core.fabric.events.connection.ServerEvents;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.CheckoutService;
import me.zyouime.holymoderation.core.service.NotificationsService;
import me.zyouime.holymoderation.core.service.PunishmentService;
import me.zyouime.holymoderation.core.states.UserState;
import net.minecraft.util.ActionResult;

public final class CheckoutModule extends Module {

    private final CheckoutService service;
    private final BanReasonLookup banReasonLookup;
    private final AnyDeskExtractor anyDeskExtractor;
    private final CheckoutChatListener chatListener;
    private final List<ModCommand> commands;

    public CheckoutModule(UserState userState, PunishmentService punishmentService, CheckoutService service, ChatService chatService, NotificationsService notifications, ModSettings settings, CheckoutPrompts checkoutPrompts, CheckoutJournal checkoutJournal) {
        this.service = service;
        this.banReasonLookup = new BanReasonLookup(chatService, notifications);
        this.anyDeskExtractor = new AnyDeskExtractor(chatService, notifications);
        this.chatListener = new CheckoutChatListener(service, banReasonLookup, anyDeskExtractor, chatService, settings);
        this.commands = List.of(
                new FreezeCommand(service, userState, "freezing"),
                new FreezeCommand(service, userState, "frz"),
                new UnfreezeCommand(service, "unfreezing"),
                new UnfreezeCommand(service, "unfrz"),
                new SendTextsCommand(service, userState),
                new StartCheckoutCommand(checkoutJournal),
                new SbanCommand(punishmentService, service),
                new EndCheckoutCommand(service, checkoutJournal, banReasonLookup, checkoutPrompts));
    }

    @Override
    public void init() {
        MessageEvent.EVENT.register(message -> {
            if (!isEnabled()) {
                return ActionResult.PASS;
            }
            return chatListener.onMessage(message);
        });
        ServerEvents.LEAVE.register(this::reset);
    }

    @Override
    public List<ModCommand> commands() {
        return commands;
    }

    @Override
    public void tick() {
        service.tick();
        banReasonLookup.tick();
    }

    @Override
    protected void onDisable() {
        reset();
    }

    private void reset() {
        service.onServerLeave();
        banReasonLookup.cancel();
        anyDeskExtractor.reset();
    }
}
