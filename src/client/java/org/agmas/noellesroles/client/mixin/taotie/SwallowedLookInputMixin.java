package org.agmas.noellesroles.client.mixin.taotie;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class SwallowedLookInputMixin {

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player
                && MinecraftClient.getInstance().player != null
                && SwallowedPlayerComponent.isPlayerSwallowed(MinecraftClient.getInstance().player)) {
            ci.cancel();
        }
    }
}
