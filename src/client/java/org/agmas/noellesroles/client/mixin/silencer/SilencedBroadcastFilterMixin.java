package org.agmas.noellesroles.client.mixin.silencer;

import dev.doctor4t.wathe.client.gui.WalkieTalkieBroadcastRenderer;
import net.minecraft.client.MinecraftClient;
import org.agmas.noellesroles.silencer.SilencedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 被静语的玩家不应该看到任何屏幕上方的广播消息
 * 包括对讲机消息和职业广播消息
 * 通过拦截 WalkieTalkieBroadcastRenderer.addMessage() 实现
 */
@Mixin(WalkieTalkieBroadcastRenderer.class)
public class SilencedBroadcastFilterMixin {

    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true, remap = false)
    private static void noellesroles$filterSilencedBroadcast(String message, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null
                && SilencedPlayerComponent.isPlayerSilenced(MinecraftClient.getInstance().player)) {
            ci.cancel();
        }
    }
}
