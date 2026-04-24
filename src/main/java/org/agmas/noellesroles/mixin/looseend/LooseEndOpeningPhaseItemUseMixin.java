package org.agmas.noellesroles.mixin.looseend;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.agmas.noellesroles.util.SwallowedInteractionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class LooseEndOpeningPhaseItemUseMixin {

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void noellesroles$blockViewerOpeningPhaseEntityUse(
            ItemStack stack,
            PlayerEntity user,
            net.minecraft.entity.LivingEntity entity,
            Hand hand,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        if (entity instanceof PlayerEntity target
                && SwallowedInteractionHelper.blocksPlayerTargetForViewer(user, target)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
