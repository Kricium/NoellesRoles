package org.agmas.noellesroles.mixin.effect;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 统一让状态效果只显示 HUD/背包图标，不显示实体粒子。
 */
@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectDisplayMixin {

    @Inject(method = "shouldShowParticles", at = @At("HEAD"), cancellable = true)
    private void noellesroles$hideParticles(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    private void noellesroles$keepIcons(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
