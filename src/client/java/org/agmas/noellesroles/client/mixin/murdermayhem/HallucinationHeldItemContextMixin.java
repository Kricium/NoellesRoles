package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationHeldItemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public class HallucinationHeldItemContextMixin {
    @Inject(method = "renderItem", at = @At("HEAD"))
    private void noellesroles$beginHeldItemContext(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode transformationMode,
            Arm arm,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            ClientHallucinationHeldItemHelper.beginHeldItemRender(player);
        } else {
            ClientHallucinationHeldItemHelper.endHeldItemRender();
        }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void noellesroles$endHeldItemContext(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode transformationMode,
            Arm arm,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        ClientHallucinationHeldItemHelper.endHeldItemRender();
    }
}
