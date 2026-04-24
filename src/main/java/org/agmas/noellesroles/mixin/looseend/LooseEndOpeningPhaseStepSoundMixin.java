package org.agmas.noellesroles.mixin.looseend;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.agmas.noellesroles.util.StealthStateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class LooseEndOpeningPhaseStepSoundMixin {

    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void noellesroles$muteLooseEndOpeningPhaseFootsteps(BlockPos pos, BlockState state, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (StealthStateHelper.shouldFullyHidePlayer(player)) {
            ci.cancel();
        }
    }
}
