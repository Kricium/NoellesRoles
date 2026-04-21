package org.agmas.noellesroles.client.mixin.looseend;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameOverlayRenderer.class)
public class LooseEndOpeningPhaseOverlayMixin {

    @WrapWithCondition(
            method = "renderOverlays",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderInWallOverlay(Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private static boolean noellesroles$disableInWallOverlayGlobally(Sprite sprite, MatrixStack matrices, net.minecraft.client.MinecraftClient client, MatrixStack overlayMatrices) {
        return false;
    }
}
