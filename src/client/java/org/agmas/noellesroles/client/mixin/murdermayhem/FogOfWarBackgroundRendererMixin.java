package org.agmas.noellesroles.client.mixin.murdermayhem;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.util.math.MathHelper;
import org.agmas.noellesroles.client.murdermayhem.FogOfWarClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class FogOfWarBackgroundRendererMixin {
    private static final float INNER_HAZE_RANGE = 6.0F;
    private static final float EDGE_BLEND_RANGE = 6.5F;
    private static final float TERRAIN_OUTER_FULL_OPACITY_RANGE = 1.25F;
    private static final float ZERO_RADIUS_FOG_END = 0.25F;

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$applyFogOfWar(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            float viewDistance,
            boolean thickFog,
            float tickDelta,
            CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null
                || !FogOfWarClientHelper.isCachedFogActive()
                || FogOfWarClientHelper.isCachedIgnoreFog()) {
            return;
        }

        float fogRadius = FogOfWarClientHelper.getRenderedFogRadius(tickDelta);
        float targetStart = fogType == BackgroundRenderer.FogType.FOG_SKY
                ? 0.0F
                : (fogRadius <= 0.0F ? 0.0F : Math.max(0.75F, fogRadius - EDGE_BLEND_RANGE - INNER_HAZE_RANGE));
        float targetEnd = fogRadius <= 0.0F ? ZERO_RADIUS_FOG_END : fogRadius + TERRAIN_OUTER_FULL_OPACITY_RANGE;
        RenderSystem.setShaderFogStart(targetStart);
        RenderSystem.setShaderFogEnd(targetEnd);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
        RenderSystem.setShaderFogColor(
                (float) FogOfWarClientHelper.getFogColorVec().x,
                (float) FogOfWarClientHelper.getFogColorVec().y,
                (float) FogOfWarClientHelper.getFogColorVec().z
        );
        ci.cancel();
    }
}
