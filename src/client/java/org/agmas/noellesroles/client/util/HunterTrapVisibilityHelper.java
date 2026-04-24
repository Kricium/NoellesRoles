package org.agmas.noellesroles.client.util;

import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.entity.HunterTrapEntity;

public final class HunterTrapVisibilityHelper {
    private HunterTrapVisibilityHelper() {
    }

    public static boolean shouldRenderForClient(HunterTrapEntity trap, PlayerEntity player) {
        if (trap.canRenderFor(player)) {
            return true;
        }

        if (!WatheClient.isInstinctEnabled() || !WatheClient.isPlayerPlayingAndAlive()) {
            return false;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        return gameWorld.isRole(player, WatheRoles.LOOSE_END) && trap.canBeSeenBy(player);
    }
}
