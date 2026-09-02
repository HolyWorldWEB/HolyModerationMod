package me.zyouime.holymoderation.mixin;

import me.zyouime.holymoderation.Main;
import me.zyouime.holymoderation.core.fabric.events.chat.CommandSendEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        Main.getModContext().connectionTracker().onGameJoin((ClientPlayNetworkHandler) (Object) this);
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void onChatCommand(String command, CallbackInfo ci) {
        if (Main.getModContext().chatService().isInternalCommand()) {
            return;
        }
        ActionResult result = CommandSendEvent.EVENT.invoker().onCommand(command);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
