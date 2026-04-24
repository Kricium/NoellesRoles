package org.agmas.noellesroles.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.FogShape;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.client.murdermayhem.FogOfWarClientHelper;

public final class FogOfWarRenderer {
    private static final float INNER_HAZE_RANGE = 6.0F;
    private static final float EDGE_BLEND_RANGE = 6.5F;
    private static final float TERRAIN_OUTER_FULL_OPACITY_RANGE = 1.25F;
    private static final float ZERO_RADIUS_FOG_END = 0.25F;

    private FogOfWarRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_SETUP.register(FogOfWarRenderer::applyFallbackFog);
        WorldRenderEvents.BEFORE_ENTITIES.register(FogOfWarRenderer::applyFallbackFog);
    }

    private static void applyFallbackFog(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || !FogOfWarClientHelper.isCachedFogActive() || FogOfWarClientHelper.isCachedIgnoreFog()) {
            return;
        }

        float viewDistance = client.gameRenderer.getViewDistance();
        float originalStart = viewDistance - MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
        float fogRadius = FogOfWarClientHelper.getRenderedFogRadius();
        float targetStart = fogRadius <= 0.0F ? 0.0F : Math.max(0.75F, fogRadius - EDGE_BLEND_RANGE - INNER_HAZE_RANGE);
        float targetEnd = fogRadius <= 0.0F ? ZERO_RADIUS_FOG_END : fogRadius + TERRAIN_OUTER_FULL_OPACITY_RANGE;
        Vec3d fogColor = FogOfWarClientHelper.getFogColorVec();

        RenderSystem.setShaderFogStart(Math.min(originalStart, targetStart));
        RenderSystem.setShaderFogEnd(targetEnd);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
        RenderSystem.setShaderFogColor((float) fogColor.x, (float) fogColor.y, (float) fogColor.z);
    }
}
