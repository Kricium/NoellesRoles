package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.config.datapack.MapRegistry;
import dev.doctor4t.wathe.config.datapack.MapRegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MapRegistry.class)
public interface MapRegistryAccessor {
    @Accessor("maps")
    Map<Identifier, MapRegistryEntry> noellesroles$getMutableMaps();
}
