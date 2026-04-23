package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class HallucinationShadowMixin {
    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$hideHallucinatedShadow(net.minecraft.client.util.math.MatrixStack matrices,
                                                            net.minecraft.client.render.VertexConsumerProvider vertexConsumers,
                                                            Entity entity,
                                                            float opacity,
                                                            float tickDelta,
                                                            net.minecraft.world.WorldView world,
                                                            float radius,
                                                            CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }
        PlayerEntity localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer == null || player == localPlayer) {
            return;
        }
        if (HallucinationClientVisibilityHelper.shouldHidePlayer(localPlayer, player)) {
            ci.cancel();
        }
    }
}
