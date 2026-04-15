package org.agmas.noellesroles.client.util;

import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.jetbrains.annotations.Nullable;

public final class InsanityNameHelper {
    private static final String OBFUSCATED_NAME = "??!?!";

    private InsanityNameHelper() {
    }

    public static boolean shouldObfuscateNames() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || WatheClient.moodComponent == null) {
            return false;
        }

        ConfigWorldComponent config = ConfigWorldComponent.KEY.get(client.player.getWorld());
        return config.insaneSeesMorphs && WatheClient.moodComponent.isLowerThanDepressed();
    }

    public static Text getVisiblePlayerName(@Nullable PlayerEntity player) {
        if (player == null) {
            return Text.literal("Unknown");
        }
        if (shouldObfuscateNames()) {
            return Text.literal(OBFUSCATED_NAME).formatted(Formatting.OBFUSCATED);
        }
        return player.getDisplayName();
    }
}
