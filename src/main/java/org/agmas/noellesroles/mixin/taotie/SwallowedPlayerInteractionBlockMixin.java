package org.agmas.noellesroles.mixin.taotie;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class SwallowedPlayerInteractionBlockMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    // This is the broad "actor is swallowed" block for normal item/block use.
    // Target-side exceptions are handled separately through SwallowedInteractionHelper.
    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedInteractItem(CallbackInfoReturnable<ActionResult> cir) {
        if (SwallowedPlayerComponent.isPlayerSwallowed(this.player)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedInteractBlock(CallbackInfoReturnable<ActionResult> cir) {
        if (SwallowedPlayerComponent.isPlayerSwallowed(this.player)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
