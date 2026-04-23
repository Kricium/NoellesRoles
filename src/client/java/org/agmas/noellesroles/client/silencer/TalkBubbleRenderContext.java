package org.agmas.noellesroles.client.silencer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class TalkBubbleRenderContext {
    private static final ThreadLocal<AbstractClientPlayerEntity> CURRENT_PLAYER = new ThreadLocal<>();

    private TalkBubbleRenderContext() {
    }

    public static void setCurrentPlayer(@Nullable AbstractClientPlayerEntity player) {
        if (player == null) {
            CURRENT_PLAYER.remove();
            return;
        }
        CURRENT_PLAYER.set(player);
    }

    @Nullable
    public static AbstractClientPlayerEntity getCurrentPlayer() {
        return CURRENT_PLAYER.get();
    }

    public static void clear() {
        CURRENT_PLAYER.remove();
    }
}
