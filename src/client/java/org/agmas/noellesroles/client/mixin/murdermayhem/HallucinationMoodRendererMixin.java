package org.agmas.noellesroles.client.mixin.murdermayhem;

import dev.doctor4t.wathe.client.gui.MoodRenderer;
import org.agmas.noellesroles.client.gui.HallucinationHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoodRenderer.class)
public abstract class HallucinationMoodRendererMixin {
    @Inject(method = "renderHud", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$hideMoodHud(net.minecraft.entity.player.PlayerEntity player,
                                                 net.minecraft.client.font.TextRenderer renderer,
                                                 net.minecraft.client.gui.DrawContext context,
                                                 net.minecraft.client.render.RenderTickCounter tickCounter,
                                                 CallbackInfo ci) {
        if (HallucinationHudRenderer.shouldSuppressSanityHud()) {
            ci.cancel();
        }
    }
}
