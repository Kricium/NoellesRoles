package org.agmas.noellesroles.client.mixin.taotie;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.agmas.noellesroles.looseend.LooseEndPlayerComponent;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class SwallowedInteractionBypassMixin {

    @Shadow
    public Entity targetedEntity;

    @Shadow
    public HitResult crosshairTarget;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void noellesroles$ignoreSwallowedPlayerForAttack(CallbackInfoReturnable<Boolean> cir) {
        this.noellesroles$clearSwallowedHitTarget();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void noellesroles$ignoreSwallowedPlayerForUse(CallbackInfo ci) {
        this.noellesroles$clearSwallowedHitTarget();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void noellesroles$ignoreOpeningPhaseTargetEveryTick(CallbackInfo ci) {
        this.noellesroles$clearSwallowedHitTarget();
    }

    private void noellesroles$clearSwallowedHitTarget() {
        if (!(this.crosshairTarget instanceof EntityHitResult entityHitResult)) {
            return;
        }
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (!(entityHitResult.getEntity() instanceof PlayerEntity player) || client.player == null) {
            return;
        }

        boolean localOpeningPhased = LooseEndPlayerComponent.KEY.get(client.player).isOpeningPhased();
        boolean targetOpeningPhased = LooseEndPlayerComponent.KEY.get(player).isOpeningPhased();
        boolean swallowedTarget = SwallowedPlayerComponent.isPlayerSwallowed(player);
        boolean viewerBlockedTarget = localOpeningPhased && !player.getUuid().equals(client.player.getUuid());
        if (!swallowedTarget
                && !targetOpeningPhased
                && !viewerBlockedTarget) {
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
