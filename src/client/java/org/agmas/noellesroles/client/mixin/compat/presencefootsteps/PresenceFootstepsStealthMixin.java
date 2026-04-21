package org.agmas.noellesroles.client.mixin.compat.presencefootsteps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.util.StealthStateHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "eu.ha3.presencefootsteps.sound.StepSoundSource$Container", remap = false)
public class PresenceFootstepsStealthMixin {

    @Shadow @Final private LivingEntity entity;

    @Inject(method = "getStepGenerator", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockPresenceFootstepsForStealthStates(CallbackInfoReturnable<Optional<?>> cir) {
        if (this.entity instanceof PlayerEntity player && StealthStateHelper.shouldFullyHidePlayer(player)) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
