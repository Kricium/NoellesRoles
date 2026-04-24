package org.agmas.noellesroles.util;

import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.looseend.LooseEndPlayerComponent;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.jetbrains.annotations.Nullable;

public final class AreaDamageImmunityHelper {
    private AreaDamageImmunityHelper() {}

    public static boolean isImmuneToAreaDamage(@Nullable PlayerEntity player) {
        return player != null && (
                SwallowedPlayerComponent.isPlayerSwallowed(player)
                        || LooseEndPlayerComponent.KEY.get(player).isOpeningPhased()
        );
    }
}
