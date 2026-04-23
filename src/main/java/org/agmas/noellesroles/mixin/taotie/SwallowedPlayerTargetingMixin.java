package org.agmas.noellesroles.mixin.taotie;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class SwallowedPlayerTargetingMixin {

    @Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableCanHitWhenSwallowed(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableAttackableWhenSwallowed(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canBeHitByProjectile", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableProjectileHitsWhenSwallowed(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
    private void noellesroles$disableCollidableWhenSwallowed(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void noellesroles$clearTargetingMarginWhenSwallowed(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "getVisibilityBoundingBox", at = @At("HEAD"), cancellable = true)
    private void noellesroles$shrinkVisibilityBoxWhenSwallowed(CallbackInfoReturnable<Box> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            cir.setReturnValue(new Box(
                    entity.getX(), entity.getY(), entity.getZ(),
                    entity.getX(), entity.getY(), entity.getZ()
            ));
        }
    }
}
