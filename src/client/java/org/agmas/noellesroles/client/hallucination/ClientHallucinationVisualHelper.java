package org.agmas.noellesroles.client.hallucination;

import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.hallucination.HallucinationEffectId;
import org.agmas.noellesroles.hallucination.HallucinationPlayerComponent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ClientHallucinationVisualHelper {
    private ClientHallucinationVisualHelper() {
    }

    public static boolean shouldScrambleSkins() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return false;
        }
        HallucinationPlayerComponent component = HallucinationPlayerComponent.KEY.get(client.player);
        return component.hasEffect(HallucinationEffectId.SCRAMBLED_SKINS);
    }

    public static boolean shouldUseShuffledAppearance(@Nullable AbstractClientPlayerEntity player) {
        if (player == null || !NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.containsKey(player.getUuid())) {
            return false;
        }
        if (shouldScrambleSkins()) {
            return true;
        }
        if (WatheClient.moodComponent == null) {
            return false;
        }
        ConfigWorldComponent config = ConfigWorldComponent.KEY.get(player.getWorld());
        return config.insaneSeesMorphs && WatheClient.moodComponent.isLowerThanDepressed();
    }

    public static @Nullable PlayerListEntry getShuffledEntry(@Nullable AbstractClientPlayerEntity player) {
        if (!shouldUseShuffledAppearance(player) || player == null) {
            return null;
        }
        UUID shuffledUuid = NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.get(player.getUuid());
        if (shuffledUuid == null) {
            return null;
        }
        return WatheClient.PLAYER_ENTRIES_CACHE.get(shuffledUuid);
    }

    public static @Nullable SkinTextures getShuffledSkinTextures(@Nullable AbstractClientPlayerEntity player) {
        PlayerListEntry entry = getShuffledEntry(player);
        return entry != null ? entry.getSkinTextures() : null;
    }
}
