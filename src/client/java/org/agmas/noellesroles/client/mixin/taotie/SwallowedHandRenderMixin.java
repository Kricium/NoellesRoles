package org.agmas.noellesroles.client.mixin.taotie;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class SwallowedHandRenderMixin {

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void noellesroles$hideSwallowedHand(Camera camera, float tickDelta, Matrix4f matrix, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null
                && SwallowedPlayerComponent.isPlayerSwallowed(MinecraftClient.getInstance().player)) {
            ci.cancel();
        }
    }
}
