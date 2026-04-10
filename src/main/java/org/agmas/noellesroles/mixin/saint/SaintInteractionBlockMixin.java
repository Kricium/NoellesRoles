package org.agmas.noellesroles.mixin.saint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import org.agmas.noellesroles.saint.SaintHelper;
import org.agmas.noellesroles.saint.SaintPlayerComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class SaintInteractionBlockMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockKarmaInteractItem(CallbackInfoReturnable<ActionResult> cir) {
        if (!SaintPlayerComponent.KEY.get(this.player).isKarmaLocked()) {
            return;
        }
        SaintHelper.sendKarmaLockedMessage(this.player);
        cir.setReturnValue(ActionResult.FAIL);
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockKarmaInteractBlock(CallbackInfoReturnable<ActionResult> cir) {
        if (!SaintPlayerComponent.KEY.get(this.player).isKarmaLocked()) {
            return;
        }
        SaintHelper.sendKarmaLockedMessage(this.player);
        cir.setReturnValue(ActionResult.FAIL);
    }

}
