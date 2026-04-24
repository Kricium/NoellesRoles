package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.config.datapack.MapRegistry;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.murdermayhem.MurderMayhemHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MapRegistry.class)
public class MapRegistryGameModeAliasMixin {
    @ModifyArg(
            method = "getMapsForGameMode",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/config/datapack/MapRegistryEntry;supportsGameMode(Lnet/minecraft/util/Identifier;)Z")
    )
    private Identifier noellesroles$aliasClassicMurderMaps(Identifier gameModeId) {
        return MurderMayhemHelper.normalizeToClassicMurder(gameModeId);
    }

    @ModifyArg(
            method = "getEligibleMapsForGameMode",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/config/datapack/MapRegistryEntry;supportsGameMode(Lnet/minecraft/util/Identifier;)Z")
    )
    private Identifier noellesroles$aliasClassicMurderEligibleMaps(Identifier gameModeId) {
        return MurderMayhemHelper.normalizeToClassicMurder(gameModeId);
    }
}
