package org.agmas.noellesroles.client.mixin.murdermayhem;

import dev.doctor4t.wathe.client.gui.StoreRenderer;
import org.agmas.noellesroles.client.gui.HallucinationHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StoreRenderer.class)
public abstract class HallucinationMoneyRendererMixin {
    @Inject(method = "renderHud", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$hideMoneyHud(net.minecraft.client.font.TextRenderer renderer,
                                                  net.minecraft.client.network.ClientPlayerEntity player,
                                                  net.minecraft.client.gui.DrawContext context,
                                                  float delta,
                                                  CallbackInfo ci) {
        if (HallucinationHudRenderer.shouldSuppressMoneyHud()) {
            ci.cancel();
        }
    }
}
