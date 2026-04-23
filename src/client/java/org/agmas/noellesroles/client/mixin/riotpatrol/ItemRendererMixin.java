package org.agmas.noellesroles.client.mixin.riotpatrol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationHeldItemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyVariable(
        method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private ItemStack noellesroles$swapHallucinationHeldItemForEntityRender(
        ItemStack value,
        LivingEntity entity,
        ItemStack stack,
        ModelTransformationMode renderMode,
        boolean leftHanded,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        World world,
        int light,
        int overlay,
        int seed
    ) {
        return ClientHallucinationHeldItemHelper.getScrambledHeldItem(value, renderMode);
    }

    @ModifyVariable(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private ItemStack noellesroles$swapHallucinationHeldItem(
        ItemStack value,
        ItemStack stack,
        ModelTransformationMode renderMode,
        boolean leftHanded,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay,
        BakedModel model
    ) {
        return ClientHallucinationHeldItemHelper.getScrambledHeldItem(value, renderMode);
    }

    @ModifyVariable(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private BakedModel noellesroles$swapRiotForkInHandModel(
        BakedModel value,
        ItemStack stack,
        ModelTransformationMode renderMode,
        boolean leftHanded,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay,
        BakedModel model
    ) {
        // Keep the inventory sprite on the normal item model and only swap to the
        // custom blockbench model while the fork is actually rendered in-hand.
        if (stack.isOf(ModItems.RIOT_FORK) && noellesroles$isHandRenderMode(renderMode)) {
            return ((FabricBakedModelManager) MinecraftClient.getInstance().getBakedModelManager()).getModel(NoellesrolesClient.RIOT_FORK_IN_HAND_MODEL_ID);
        }

        return value;
    }

    private static boolean noellesroles$isHandRenderMode(ModelTransformationMode renderMode) {
        return renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND
            || renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
            || renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
            || renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
    }
}
