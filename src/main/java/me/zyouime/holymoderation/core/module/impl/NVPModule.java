package me.zyouime.holymoderation.core.module.impl;

import me.zyouime.holymoderation.core.fabric.events.chat.MessageEvent;
import me.zyouime.holymoderation.core.module.Module;
import me.zyouime.holymoderation.core.nvp.NVPChatListener;
import me.zyouime.holymoderation.core.service.NVPService;
import net.minecraft.util.ActionResult;

public class NVPModule extends Module {

    private final NVPChatListener nvpChatListener;

    public NVPModule(NVPService nvpService) {
        nvpChatListener = new NVPChatListener(nvpService);
    }

    @Override
    public void init() {
        MessageEvent.EVENT.register(text -> {
            if (isEnabled()) {
                nvpChatListener.onMessage(text);
            }
            return ActionResult.PASS;
        });
    }
}
