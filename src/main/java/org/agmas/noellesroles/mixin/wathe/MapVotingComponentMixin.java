package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.cca.MapVotingComponent;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.deatharena.DeathArenaStateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
