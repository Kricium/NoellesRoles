package org.agmas.noellesroles.mixin.stealth;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.util.StealthStateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class StealthSprintingParticlesMixin {

    @Inject(method = "shouldSpawnSprintingParticles", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableSprintingParticlesForStealthPlayers(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && StealthStateHelper.shouldFullyHidePlayer(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "spawnSprintingParticles", at = @At("HEAD"), cancellable = true)
    private void noellesroles$cancelSprintingParticleSpawnForStealthPlayers(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && StealthStateHelper.shouldFullyHidePlayer(player)) {
            ci.cancel();
        }
    }
}
