package org.agmas.noellesroles.client.hallucination;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.hallucination.HallucinationHelper;

public final class HallucinationClientVisibilityHelper {
    private HallucinationClientVisibilityHelper() {
    }

    public static boolean shouldHidePlayer(PlayerEntity localPlayer, PlayerEntity target) {
        if (localPlayer == null || target == null || localPlayer == target) {
            return false;
        }
        return HallucinationHelper.isHallucinationTargetHidden(localPlayer, target);
    }

    public static boolean shouldHideEntity(Entity entity) {
        if (!(entity instanceof PlayerEntity target)) {
            return false;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null && shouldHidePlayer(client.player, target);
    }
}
