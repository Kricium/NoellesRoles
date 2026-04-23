package org.agmas.noellesroles.mixin.collision;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.Entity;
import org.agmas.noellesroles.util.NoCollisionStateHelper;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 通用碰撞禁用 Mixin（Entity 层）。
 * 处理以下情况的 collidesWith：
 * - 龙舌兰无碰撞效果（NO_COLLISION 药水）
 * - 变形者尸体模式（corpseMode）
 * - 清道夫隐藏尸体
 */
@Mixin(Entity.class)
public class EntityCollisionMixin {

    @WrapMethod(method = "collidesWith")
    private boolean noellesroles$disableCollision(Entity other, Operation<Boolean> original) {
        Entity self = (Entity) (Object) this;

        if (NoCollisionStateHelper.shouldDisableCollision(self) || NoCollisionStateHelper.shouldDisableCollision(other)) {
            return false;
        }

        return original.call(other);
    }
}
