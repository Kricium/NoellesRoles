package org.agmas.noellesroles.mixin.collision;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.agmas.noellesroles.util.NoCollisionStateHelper;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 通用碰撞禁用 Mixin（LivingEntity 层）。
 * 处理以下情况的 isPushable / pushAway：
 * - 龙舌兰无碰撞效果（NO_COLLISION 药水）
 * - 变形者尸体模式（corpseMode）
 * - 清道夫隐藏尸体
 */
@Mixin(LivingEntity.class)
public class LivingEntityCollisionMixin {

    @WrapMethod(method = "isPushable")
    private boolean noellesroles$disablePushable(Operation<Boolean> original) {
        if (shouldDisableCollision()) {
            return false;
        }
        return original.call();
    }

    @WrapMethod(method = "pushAway")
    private void noellesroles$disablePushAway(Entity entity, Operation<Void> original) {
        if (NoCollisionStateHelper.shouldDisableCollision((Entity) (Object) this) || NoCollisionStateHelper.shouldDisableCollision(entity)) {
            return;
        }
        original.call(entity);
    }

    private boolean shouldDisableCollision() {
        return NoCollisionStateHelper.shouldDisableCollision((Entity) (Object) this);
    }
}
