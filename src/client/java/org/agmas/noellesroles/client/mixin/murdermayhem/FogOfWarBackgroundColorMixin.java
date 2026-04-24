package org.agmas.noellesroles.client.mixin.murdermayhem;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import org.agmas.noellesroles.client.murdermayhem.FogOfWarClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class FogOfWarBackgroundColorMixin {
    @Shadow
    private static float red;

    @Shadow
    private static float green;

    @Shadow
    private static float blue;

    @Inject(method = "render", at = @At("TAIL"))
    private static void noellesroles$applyFogOfWarColor(
            Camera camera,
            float tickDelta,
            ClientWorld world,
            int viewDistance,
            float skyDarkness,
            CallbackInfo ci
    ) {
        if (world == null || !FogOfWarClientHelper.isCachedFogActive() || FogOfWarClientHelper.isCachedIgnoreFog()) {
            return;
        }

        var fogColor = FogOfWarClientHelper.getFogColorVec();
        red = (float) fogColor.x;
        green = (float) fogColor.y;
        blue = (float) fogColor.z;
    }

    @Inject(method = "applyFogColor", at = @At("TAIL"))
    private static void noellesroles$forceFogOfWarColor(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || !FogOfWarClientHelper.isCachedFogActive() || FogOfWarClientHelper.isCachedIgnoreFog()) {
            return;
        }

        var fogColor = FogOfWarClientHelper.getFogColorVec();
        RenderSystem.setShaderFogColor((float) fogColor.x, (float) fogColor.y, (float) fogColor.z);
    }
}
