package org.agmas.noellesroles.mixin.looseend;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.agmas.noellesroles.looseend.LooseEndPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class LooseEndOpeningPhaseEntityMixin {

    @Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableCanHitDuringLooseEndOpeningPhase(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableAttackableDuringLooseEndOpeningPhase(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canBeHitByProjectile", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableProjectileHitsDuringLooseEndOpeningPhase(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableCollidableDuringLooseEndOpeningPhase(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void noellesroles$clearTargetingMarginDuringLooseEndOpeningPhase(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "getVisibilityBoundingBox", at = @At("HEAD"), cancellable = true)
    private void noellesroles$shrinkVisibilityBoxDuringLooseEndOpeningPhase(CallbackInfoReturnable<Box> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()) {
            cir.setReturnValue(new Box(
                    entity.getX(), entity.getY(), entity.getZ(),
                    entity.getX(), entity.getY(), entity.getZ()
            ));
        }
    }

    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void noellesroles$hidePlayersFromLooseEndViewer(PlayerEntity viewer, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (!(viewer instanceof PlayerEntity)) {
            return;
        }

        if (entity instanceof PlayerEntity targetPlayer
                && LooseEndPlayerComponent.KEY.get(targetPlayer).isOpeningPhased()
                && !targetPlayer.getUuid().equals(viewer.getUuid())) {
            cir.setReturnValue(true);
            return;
        }

        if (LooseEndPlayerComponent.KEY.get(viewer).isOpeningPhased()
                && entity instanceof PlayerEntity targetPlayer
                && !targetPlayer.getUuid().equals(viewer.getUuid())) {
            cir.setReturnValue(true);
        }
    }
}
