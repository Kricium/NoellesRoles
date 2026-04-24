package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.agmas.noellesroles.client.hallucination.ClientHallucinationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class HallucinationCrosshairTargetMixin {
    @Shadow
    public Entity targetedEntity;

    @Shadow
    public HitResult crosshairTarget;

    @Inject(method = "tick", at = @At("TAIL"))
    private void noellesroles$clearHiddenCrosshairTargetEveryTick(CallbackInfo ci) {
        this.noellesroles$clearHiddenPlayerHitTarget();
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void noellesroles$clearHiddenCrosshairTargetForAttack(CallbackInfoReturnable<Boolean> cir) {
        this.noellesroles$clearHiddenPlayerHitTarget();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void noellesroles$clearHiddenCrosshairTargetForUse(CallbackInfo ci) {
        this.noellesroles$clearHiddenPlayerHitTarget();
    }

    private void noellesroles$clearHiddenPlayerHitTarget() {
        if (!(this.crosshairTarget instanceof EntityHitResult entityHitResult)) {
            return;
        }
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player == null) {
            return;
        }
        if (!(entityHitResult.getEntity() instanceof PlayerEntity player)) {
            return;
        }
        if (ClientHallucinationState.isDummyEntity(player)) {
            return;
        }
        if (!HallucinationClientVisibilityHelper.shouldHidePlayer(client.player, player)) {
            return;
        }

        this.targetedEntity = null;
        this.crosshairTarget = BlockHitResult.createMissed(
                entityHitResult.getPos(),
                Direction.UP,
                BlockPos.ofFloored(entityHitResult.getPos())
        );
    }
}
