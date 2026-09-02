package me.zyouime.holymoderation.mixin;

import me.zyouime.holymoderation.core.fabric.events.chat.MessageDecorateEvent;
import me.zyouime.holymoderation.core.fabric.events.chat.MessageEvent;
import me.zyouime.holymoderation.core.providers.MinecraftProvider;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (MessageEvent.EVENT.invoker().onMessage(message) == ActionResult.FAIL) {
            ci.cancel();
            return;
        }
        if (overlay) {
            return;
        }
        Text decorated = MessageDecorateEvent.EVENT.invoker().decorate(message);
        if (decorated == message) {
            return;
        }
        ci.cancel();
        MinecraftProvider.client().inGameHud.getChatHud().addMessage(decorated);
    }
}
