package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashSet;
import java.util.UUID;

@Mixin(GameWorldComponent.class)
public interface GameWorldComponentAccessor {
    @Accessor("deadPlayers")
    HashSet<UUID> noellesroles$getDeadPlayers();
}
