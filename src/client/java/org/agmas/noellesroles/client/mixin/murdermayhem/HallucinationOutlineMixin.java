package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;
import org.agmas.noellesroles.client.murdermayhem.FogOfWarClientHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class HallucinationOutlineMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockFogOrHiddenOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player == null || entity == null) {
            return;
        }
        if (HallucinationClientVisibilityHelper.shouldHideEntity(entity)) {
            cir.setReturnValue(false);
            return;
        }
        if (ClientHallucinationState.isManagedDummyEntity(entity)) {
            return;
        }
        if (FogOfWarClientHelper.shouldBlockKillerInstinctEntity(client.player, entity)) {
            cir.setReturnValue(false);
        }
    }
}
