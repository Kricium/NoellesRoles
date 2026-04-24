package org.agmas.noellesroles.client.mixin.looseend;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.doctor4t.wathe.client.gui.HudHeaderLayout;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import dev.doctor4t.wathe.client.gui.TimeRenderer;
import org.agmas.noellesroles.client.gui.HallucinationHudRenderer;
import org.agmas.noellesroles.client.gui.TopCenterHudAnchor;
import org.agmas.noellesroles.client.gui.LooseEndsRadarHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.TimeRenderer")
public abstract class WatheTimeRendererMixin {
    @Inject(method = "renderHud", at = @At("HEAD"))
    private static void noellesroles$trackTopCenterHeader(TextRenderer renderer, net.minecraft.client.network.ClientPlayerEntity player, DrawContext context, float delta, HudHeaderLayout layout, CallbackInfo ci) {
        TopCenterHudAnchor.includeLayout(layout);
    }

    @WrapWithCondition(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/client/gui/TimeRenderer$TimeNumberRenderer;render(Lnet/minecraft/class_327;Lnet/minecraft/class_332;IIIF)V"
            )
    )
    private static boolean noellesroles$skipDefaultTimeRenderer(TimeRenderer.TimeNumberRenderer instance, TextRenderer renderer, DrawContext context, int x, int y, int colour, float delta) {
        return !LooseEndsRadarHudRenderer.shouldReplaceDefaultTimeHud()
                && !HallucinationHudRenderer.shouldSuppressTimeHud();
    }

}
