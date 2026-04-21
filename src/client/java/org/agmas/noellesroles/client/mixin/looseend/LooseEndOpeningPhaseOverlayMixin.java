package org.agmas.noellesroles.client.mixin.looseend;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.agmas.noellesroles.looseend.LooseEndPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class LooseEndOpeningPhaseOverlayMixin {

    @Inject(method = "renderOverlays", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$hideInWallOverlayDuringLooseEndOpeningPhase(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (client.player != null && LooseEndPlayerComponent.KEY.get(client.player).isOpeningPhased()) {
            ci.cancel();
        }
    }
}
