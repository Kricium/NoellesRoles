package org.agmas.noellesroles.looseend;

import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.agmas.noellesroles.util.AreaDamageImmunityHelper;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class LooseEndsRadarHelper {
    private LooseEndsRadarHelper() {}

    public static boolean isActiveLooseEnds(World world) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(world);
        return gameWorld.getGameMode() == WatheGameModes.LOOSE_ENDS
                && gameWorld.getGameStatus() == GameWorldComponent.GameStatus.ACTIVE;
    }

    public static boolean tryStartManualScan(ServerWorld world) {
        if (!isActiveLooseEnds(world)) {
            return false;
        }

        LooseEndsRadarWorldComponent radarComponent = LooseEndsRadarWorldComponent.KEY.get(world);
        if (radarComponent.isScanning()) {
            return false;
        }

        radarComponent.startScan();
        LooseEndsRadarWorldComponent.KEY.sync(world);
        return true;
    }

    public static void applyRadarGlow(ServerWorld world, @Nullable UUID excludedPlayerUuid) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!GameFunctions.isPlayerPlayingAndAlive(player)) {
                continue;
            }
            if (excludedPlayerUuid != null && excludedPlayerUuid.equals(player.getUuid())) {
                continue;
            }
            if (AreaDamageImmunityHelper.isImmuneToAreaDamage(player)) {
                continue;
            }

            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    LooseEndsRadarWorldComponent.SCAN_TICKS,
                    0,
                    false,
                    false,
                    true
            ));
        }
    }
}
