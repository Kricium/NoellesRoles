package org.agmas.noellesroles.client.mixin.murdermayhem;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import org.agmas.noellesroles.client.murdermayhem.FogOfWarClientHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class FogOfWarSkyOverlayMixin {
    private static final float SKY_OVERLAY_SIZE = 384.0F;
    private static final float SKY_OVERLAY_TOP = 48.0F;
    private static final float SKY_OVERLAY_BOTTOM = -384.0F;

    @Inject(method = "renderSky", at = @At("TAIL"))
    private void noellesroles$renderFogOfWarLowerSky(
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            float tickDelta,
            net.minecraft.client.render.Camera camera,
            boolean thickFog,
            Runnable fogCallback,
            CallbackInfo ci
    ) {
        if (!FogOfWarClientHelper.isCachedFogActive() || FogOfWarClientHelper.isCachedIgnoreFog()) {
            return;
        }

        int fogColor = FogOfWarClientHelper.getFogColor();
        int red = (fogColor >> 16) & 0xFF;
        int green = (fogColor >> 8) & 0xFF;
        int blue = fogColor & 0xFF;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        addQuad(buffer, positionMatrix,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, -SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, -SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, -SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, -SKY_OVERLAY_SIZE,
                red, green, blue);
        addQuad(buffer, positionMatrix,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, SKY_OVERLAY_SIZE,
                red, green, blue);
        addQuad(buffer, positionMatrix,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, -SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, -SKY_OVERLAY_SIZE,
                red, green, blue);
        addQuad(buffer, positionMatrix,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, -SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, -SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, SKY_OVERLAY_SIZE,
                red, green, blue);
        addQuad(buffer, positionMatrix,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, -SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, -SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_TOP, SKY_OVERLAY_SIZE,
                red, green, blue);
        addQuad(buffer, positionMatrix,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, SKY_OVERLAY_SIZE,
                SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, -SKY_OVERLAY_SIZE,
                -SKY_OVERLAY_SIZE, SKY_OVERLAY_BOTTOM, -SKY_OVERLAY_SIZE,
                red, green, blue);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void addQuad(
            BufferBuilder buffer,
            Matrix4f matrix,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3,
            float x4,
            float y4,
            float z4,
            int red,
            int green,
            int blue
    ) {
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, 255);
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, 255);
        buffer.vertex(matrix, x3, y3, z3).color(red, green, blue, 255);
        buffer.vertex(matrix, x4, y4, z4).color(red, green, blue, 255);
    }
}
