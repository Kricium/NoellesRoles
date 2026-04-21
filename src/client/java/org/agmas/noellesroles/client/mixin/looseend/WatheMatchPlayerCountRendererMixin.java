package org.agmas.noellesroles.client.mixin.looseend;

import dev.doctor4t.wathe.client.gui.HudHeaderLayout;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.agmas.noellesroles.client.gui.LooseEndsRadarHudRenderer;
import org.agmas.noellesroles.client.gui.TopCenterHudAnchor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.MatchPlayerCountRenderer")
public abstract class WatheMatchPlayerCountRendererMixin {
    @Inject(method = "renderHud", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$hideDefaultMatchPlayerCount(TextRenderer renderer, DrawContext context, HudHeaderLayout layout, CallbackInfo ci) {
        TopCenterHudAnchor.includeLayout(layout);
        if (LooseEndsRadarHudRenderer.shouldHideDefaultMatchPlayerCount()) {
            ci.cancel();
        }
    }
}
