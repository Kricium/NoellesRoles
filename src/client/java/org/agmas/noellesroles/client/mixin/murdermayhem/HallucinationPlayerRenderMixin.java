package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class HallucinationPlayerRenderMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noellesroles$hideHallucinatedPlayer(
            AbstractClientPlayerEntity player,
            float entityYaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        var localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer == null || player == localPlayer) {
            return;
        }
        if (HallucinationClientVisibilityHelper.shouldHidePlayer(localPlayer, player)) {
            ci.cancel();
        }
    }
}
