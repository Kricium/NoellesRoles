package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.cca.MapVotingComponent;
import dev.doctor4t.wathe.api.WatheGameModes;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.deatharena.DeathArenaStateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MapVotingComponent.class)
public abstract class MapVotingComponentMixin {
    @Shadow
    public abstract void sync();

    @Inject(method = "startMapVotingForMode", at = @At("RETURN"))
    private void noellesroles$filterDeathArenaFromMapVote(Identifier gameModeId, int playerCount, CallbackInfo ci) {
        Identifier arenaMapId = DeathArenaStateHelper.getConfiguredMapId();
        Identifier arenaDimensionId = DeathArenaStateHelper.getArenaDimensionId();
        if (arenaMapId == null && arenaDimensionId == null) {
            return;
        }

        MapVotingComponentAccessor accessor = (MapVotingComponentAccessor) this;
        boolean removedAvailable = accessor.noellesroles$getAvailableMaps().removeIf(entry ->
                (arenaMapId != null && arenaMapId.equals(entry.mapId()))
                        || (arenaDimensionId != null && arenaDimensionId.equals(entry.dimensionId())));
        boolean removedUnavailable = accessor.noellesroles$getUnavailableMaps().removeIf(entry ->
                arenaDimensionId != null && arenaDimensionId.equals(entry.dimensionId()));

        if (!removedAvailable && !removedUnavailable) {
            return;
        }

        accessor.noellesroles$setVoteCounts(new int[accessor.noellesroles$getAvailableMaps().size()]);
        if (accessor.noellesroles$getSelectedMapIndex() >= accessor.noellesroles$getAvailableMaps().size()) {
            accessor.noellesroles$setSelectedMapIndex(-1);
        }
        sync();
    }

    @Inject(method = "buildModeChoices", at = @At("RETURN"))
    private void noellesroles$reorderMurderMayhemMode(CallbackInfo ci) {
        MapVotingComponentAccessor accessor = (MapVotingComponentAccessor) this;
        List<MapVotingComponent.VotingModeEntry> availableModes = accessor.noellesroles$getAvailableModes();
        int classicIndex = noellesroles$indexOfMode(availableModes, WatheGameModes.MURDER_ID);
        int mayhemIndex = noellesroles$indexOfMode(availableModes, Noellesroles.MURDER_MAYHEM_ID);
        if (classicIndex < 0 || mayhemIndex < 0) {
            return;
        }

        int looseEndsIndex = noellesroles$indexOfMode(availableModes, WatheGameModes.LOOSE_ENDS_ID);
        MapVotingComponent.VotingModeEntry mayhemEntry = availableModes.remove(mayhemIndex);
        int insertIndex = classicIndex + 1;
        if (mayhemIndex < insertIndex) {
            insertIndex--;
        }
        if (looseEndsIndex >= 0) {
            if (mayhemIndex < looseEndsIndex) {
                looseEndsIndex--;
            }
            insertIndex = Math.min(insertIndex, looseEndsIndex);
        }
        insertIndex = Math.max(0, Math.min(insertIndex, availableModes.size()));
        availableModes.add(insertIndex, mayhemEntry);
    }

    private static int noellesroles$indexOfMode(List<MapVotingComponent.VotingModeEntry> availableModes, Identifier gameModeId) {
        for (int i = 0; i < availableModes.size(); i++) {
            if (gameModeId.equals(availableModes.get(i).gameModeId())) {
                return i;
            }
        }
        return -1;
    }
}
