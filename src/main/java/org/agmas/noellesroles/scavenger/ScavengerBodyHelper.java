package org.agmas.noellesroles.scavenger;

import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ScavengerBodyHelper {

    private ScavengerBodyHelper() {}

    public static boolean isHiddenBody(@Nullable Entity entity) {
        if (!(entity instanceof PlayerBodyEntity body)) {
            return false;
        }

        UUID playerUuid = body.getPlayerUuid();
        if (playerUuid == null) {
            return false;
        }

        return HiddenBodiesWorldComponent.KEY.get(body.getWorld()).isHidden(playerUuid);
    }
}
