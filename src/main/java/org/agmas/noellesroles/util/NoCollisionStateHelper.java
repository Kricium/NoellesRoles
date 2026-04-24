package org.agmas.noellesroles.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.ModEffects;
import org.agmas.noellesroles.looseend.LooseEndPlayerComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.scavenger.ScavengerBodyHelper;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;

public final class NoCollisionStateHelper {

    private NoCollisionStateHelper() {}

    public static boolean shouldDisableCollision(Entity entity) {
        if (ScavengerBodyHelper.isHiddenBody(entity)) {
            return true;
        }
        if (entity instanceof LivingEntity living && living.hasStatusEffect(ModEffects.NO_COLLISION)) {
            return true;
        }
        if (entity instanceof PlayerEntity player) {
            MorphlingPlayerComponent comp = MorphlingPlayerComponent.KEY.get(player);
            return comp.corpseMode
                    || SwallowedPlayerComponent.isPlayerSwallowed(player)
                    || LooseEndPlayerComponent.KEY.get(player).isOpeningPhased();
        }
        return false;
    }
}
