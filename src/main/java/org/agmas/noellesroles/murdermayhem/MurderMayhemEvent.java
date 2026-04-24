package org.agmas.noellesroles.murdermayhem;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public interface MurderMayhemEvent {
    Identifier id();

    String displayNameKey();

    String descriptionKey();

    default void onRoundInitialized(ServerWorld world, GameWorldComponent gameComponent) {
    }

    default boolean onRoundTick(ServerWorld world, GameWorldComponent gameComponent) {
        return false;
    }

    default void onRoundFinalize(ServerWorld world, GameWorldComponent gameComponent) {
    }
}
