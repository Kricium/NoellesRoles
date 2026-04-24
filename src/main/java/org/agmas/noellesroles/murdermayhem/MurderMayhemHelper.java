package org.agmas.noellesroles.murdermayhem;

import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

public final class MurderMayhemHelper {
    private MurderMayhemHelper() {
    }

    public static boolean isMurderMayhemId(Identifier gameModeId) {
        return Noellesroles.MURDER_MAYHEM_ID.equals(gameModeId);
    }

    public static boolean isMurderMayhem(GameMode gameMode) {
        return gameMode != null && isMurderMayhemId(gameMode.identifier);
    }

    public static boolean isClassicMurderCompatible(Identifier gameModeId) {
        return WatheGameModes.MURDER_ID.equals(gameModeId) || isMurderMayhemId(gameModeId);
    }

    public static boolean isClassicMurderCompatible(GameMode gameMode) {
        return gameMode != null && isClassicMurderCompatible(gameMode.identifier);
    }

    public static Identifier normalizeToClassicMurder(Identifier gameModeId) {
        return isMurderMayhemId(gameModeId) ? WatheGameModes.MURDER_ID : gameModeId;
    }

    public static boolean isMurderMayhemWorld(ServerWorld world) {
        return isMurderMayhem(GameWorldComponent.KEY.get(world).getGameMode());
    }
}
