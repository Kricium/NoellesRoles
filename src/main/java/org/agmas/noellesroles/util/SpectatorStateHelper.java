package org.agmas.noellesroles.util;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;

public final class SpectatorStateHelper {
    private SpectatorStateHelper() {
    }

    public static boolean isFakeSpectator(PlayerEntity player) {
        return player != null && SwallowedPlayerComponent.isPlayerSwallowed(player);
    }

    public static boolean isRealSpectator(PlayerEntity player) {
        return player != null && player.isSpectator() && !isFakeSpectator(player);
    }

    public static boolean isSpectatorLike(PlayerEntity player) {
        return isRealSpectator(player) || isFakeSpectator(player);
    }

    public static boolean isInGameRealSpectator(PlayerEntity player, GameWorldComponent gameWorld) {
        return player != null && gameWorld != null && gameWorld.isRunning() && isRealSpectator(player);
    }
}
