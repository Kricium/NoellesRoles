package org.agmas.noellesroles.client.mixin.taotie;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class SwallowedHotbarScrollMixin {

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedHotbarScroll(double scrollAmount, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null
                && SwallowedPlayerComponent.isPlayerSwallowed(MinecraftClient.getInstance().player)) {
            ci.cancel();
        }
    }
}
