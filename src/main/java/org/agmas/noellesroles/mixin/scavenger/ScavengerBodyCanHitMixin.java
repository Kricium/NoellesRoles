package org.agmas.noellesroles.mixin.scavenger;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.LivingEntity;
import org.agmas.noellesroles.scavenger.ScavengerBodyHelper;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 尸体准星穿透 Mixin。
 * 所有尸体都不再阻挡普通实体命中，避免挡住后方方块、掉落物和常规右键交互。
 * 尸体专用交互改走独立的尸体射线检测与可见性校验。
 */
@Mixin(LivingEntity.class)
public class ScavengerBodyCanHitMixin {

    @WrapMethod(method = "canHit")
    private boolean noellesroles$disableScavengerBodyTargeting(Operation<Boolean> original) {
        if ((Object) this instanceof dev.doctor4t.wathe.entity.PlayerBodyEntity) {
            return false;
        }
        return original.call();
    }
}
