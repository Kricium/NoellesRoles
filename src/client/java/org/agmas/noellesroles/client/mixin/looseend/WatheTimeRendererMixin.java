package org.agmas.noellesroles.client.mixin.looseend;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import dev.doctor4t.wathe.client.gui.TimeRenderer;
import org.agmas.noellesroles.client.gui.LooseEndsRadarHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.doctor4t.wathe.client.gui.TimeRenderer")
public abstract class WatheTimeRendererMixin {
    @WrapWithCondition(
            method = "renderHud",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/client/gui/TimeRenderer$TimeNumberRenderer;render(Lnet/minecraft/class_327;Lnet/minecraft/class_332;IIIF)V"
            )
    )
    private static boolean noellesroles$skipDefaultTimeRenderer(TimeRenderer.TimeNumberRenderer instance, TextRenderer renderer, DrawContext context, int x, int y, int colour, float delta) {
        return !LooseEndsRadarHudRenderer.shouldReplaceDefaultTimeHud();
    }

}
