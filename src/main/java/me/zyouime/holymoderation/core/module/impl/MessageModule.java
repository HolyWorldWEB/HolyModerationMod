package me.zyouime.holymoderation.core.module.impl;

import me.zyouime.holymoderation.config.setting.impl.ModSettings;
import me.zyouime.holymoderation.core.fabric.events.chat.MessageDecorateEvent;
import me.zyouime.holymoderation.core.message.ChatDecorator;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.service.ChatService;
import me.zyouime.holymoderation.core.service.CheckoutService;


public final class MessageModule extends Module {

    private final ChatDecorator decorator;

    public MessageModule(CheckoutService checkoutService, ChatService chatService, ModSettings settings) {
        this.decorator = new ChatDecorator(checkoutService, chatService, settings);
    }

    @Override
    public void init() {
        MessageDecorateEvent.EVENT.register(message -> {
            if (!isEnabled()) {
                return message;
            }
            return decorator.decorate(message);
        });
    }
}
