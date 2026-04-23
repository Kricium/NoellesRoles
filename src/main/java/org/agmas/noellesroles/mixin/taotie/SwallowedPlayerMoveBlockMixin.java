package org.agmas.noellesroles.mixin.taotie;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class SwallowedPlayerMoveBlockMixin {
    @WrapMethod(method = "travel")
    private void noellesroles$blockSwallowedTravel(Vec3d movementInput, Operation<Void> original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            original.call(Vec3d.ZERO);
            return;
        }
        original.call(movementInput);
    }
}
