package org.agmas.noellesroles.client.renderer;

import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.mixin.riotpatrol.ItemRendererInvoker;
import org.agmas.noellesroles.client.util.HunterTrapVisibilityHelper;
import org.agmas.noellesroles.entity.HunterTrapEntity;

public class HunterTrapEntityRenderer extends EntityRenderer<HunterTrapEntity> {
    private final ItemRenderer itemRenderer;

    public HunterTrapEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(HunterTrapEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        // Baked item quads use block-style 0..16 coordinates with a corner origin,
        // while entity rendering is centered on the entity position.
        matrices.translate(-0.5F, 0.01F, -0.5F);

        ItemStack itemStack = ModItems.HUNTER_TRAP.getDefaultStack();
        BakedModel bakedModel = ((FabricBakedModelManager) MinecraftClient.getInstance().getBakedModelManager())
            .getModel(NoellesrolesClient.HUNTER_TRAP_PLACED_MODEL_ID);
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(
            vertexConsumers,
            RenderLayer.getEntityCutoutNoCull(this.getTexture(entity)),
            true,
            itemStack.hasGlint()
        );
        ItemRendererInvoker itemRendererInvoker = (ItemRendererInvoker) this.itemRenderer;
        Random random = Random.create();

        random.setSeed(42L);
        itemRendererInvoker.noellesroles$renderBakedItemQuads(
            matrices,
            vertexConsumer,
            bakedModel.getQuads(null, null, random),
            itemStack,
            light,
            OverlayTexture.DEFAULT_UV
        );

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            itemRendererInvoker.noellesroles$renderBakedItemQuads(
                matrices,
                vertexConsumer,
                bakedModel.getQuads(null, direction, random),
                itemStack,
                light,
                OverlayTexture.DEFAULT_UV
            );
        }
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public boolean shouldRender(HunterTrapEntity entity, Frustum frustum, double x, double y, double z) {
        var player = MinecraftClient.getInstance().player;
        return player != null && HunterTrapVisibilityHelper.shouldRenderForClient(entity, player);
    }

    @Override
    public Identifier getTexture(HunterTrapEntity entity) {
        return net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}
