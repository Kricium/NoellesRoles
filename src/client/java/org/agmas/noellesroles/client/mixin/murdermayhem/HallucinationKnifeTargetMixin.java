package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.agmas.noellesroles.hallucination.HallucinationHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.doctor4t.wathe.item.KnifeItem")
public class HallucinationKnifeTargetMixin {
    @Inject(method = "getKnifeTarget", at = @At("RETURN"), cancellable = true, remap = false)
    private static void noellesroles$clearHiddenKnifeTarget(
            PlayerEntity user,
            CallbackInfoReturnable<HitResult> cir
    ) {
        if (!(cir.getReturnValue() instanceof EntityHitResult entityHitResult)) {
            return;
        }
        if (!(entityHitResult.getEntity() instanceof PlayerEntity target) || target.getUuid().equals(user.getUuid())) {
            return;
        }
        if (!HallucinationHelper.isHallucinationTargetHidden(user, target)) {
            return;
        }

        cir.setReturnValue(BlockHitResult.createMissed(
                entityHitResult.getPos(),
                Direction.UP,
                BlockPos.ofFloored(entityHitResult.getPos())
        ));
    }
}
