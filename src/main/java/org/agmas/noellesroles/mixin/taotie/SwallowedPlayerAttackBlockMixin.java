package org.agmas.noellesroles.mixin.taotie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.util.SwallowedInteractionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class SwallowedPlayerAttackBlockMixin {
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockSwallowedAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) {
            return;
        }
        if (SwallowedInteractionHelper.blocksActor(player)) {
            ci.cancel();
            return;
        }
        if (SwallowedInteractionHelper.blocksTarget(target)) {
            ci.cancel();
        }
    }
}
