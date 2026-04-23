package org.agmas.noellesroles.mixin.taotie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.agmas.noellesroles.util.SwallowedInteractionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class SwallowedPlayerEntityInteractionBlockMixin {

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockInteractionWithSwallowedPlayers(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (SwallowedInteractionHelper.blocksActor(player)) {
            cir.setReturnValue(ActionResult.FAIL);
            return;
        }
        if (SwallowedInteractionHelper.blocksTarget(entity)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
