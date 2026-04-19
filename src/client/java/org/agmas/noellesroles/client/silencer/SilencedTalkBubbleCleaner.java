package org.agmas.noellesroles.client.silencer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.agmas.noellesroles.silencer.SilencedPlayerComponent;

import java.lang.reflect.Method;
import java.util.List;

public final class SilencedTalkBubbleCleaner {
    private static final String TALK_BUBBLES_MOD_ID = "talkbubbles";
    private static Method setChatTextMethod;
    private static boolean setChatTextLookupAttempted;

    private SilencedTalkBubbleCleaner() {
    }

    public static void tick(MinecraftClient client) {
        if (client == null || client.world == null || !FabricLoader.getInstance().isModLoaded(TALK_BUBBLES_MOD_ID)) {
            return;
        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (!SilencedPlayerComponent.isPlayerSilenced(player)) {
                continue;
            }
            clearBubbleReflectively(player);
        }
    }

    public static void clearBubbleReflectively(AbstractClientPlayerEntity player) {
        Method method = resolveSetChatTextMethod(player);
        if (method == null) {
            return;
        }
        try {
            method.invoke(player, (List<String>) null, 0, 0, 0);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static Method resolveSetChatTextMethod(AbstractClientPlayerEntity player) {
        if (setChatTextMethod != null) {
            return setChatTextMethod;
        }
        if (setChatTextLookupAttempted) {
            return null;
        }

        setChatTextLookupAttempted = true;
        try {
            setChatTextMethod = player.getClass().getMethod("setChatText", List.class, int.class, int.class, int.class);
            return setChatTextMethod;
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
