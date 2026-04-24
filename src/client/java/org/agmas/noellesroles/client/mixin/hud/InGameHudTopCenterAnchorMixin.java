package org.agmas.noellesroles.client.mixin.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.agmas.noellesroles.client.gui.DeathArenaHintOverlay;
import org.agmas.noellesroles.client.gui.HallucinationHudRenderer;
import org.agmas.noellesroles.client.gui.TopCenterHudAnchor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudTopCenterAnchorMixin {
    @Shadow public abstract net.minecraft.client.font.TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("HEAD"))
    private void noellesroles$beginTopCenterAnchorFrame(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        TopCenterHudAnchor.beginFrame();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void noellesroles$renderDeathArenaHint(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        HallucinationHudRenderer.render(context, tickCounter);
        DeathArenaHintOverlay.render(context, getTextRenderer());
    }
}
