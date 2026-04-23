package org.agmas.noellesroles.client.gui;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.util.SpectatorStateHelper;

public final class DeathArenaHintOverlay {
    private DeathArenaHintOverlay() {
    }

    public static void render(DrawContext context, TextRenderer textRenderer) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.currentScreen != null || NoellesrolesClient.deathArenaToggleBind == null) {
            return;
        }

        boolean inArena = NoellesrolesClient.isDeathArenaActiveForClientPlayer();
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        if (!inArena && !gameWorld.isRunning()) {
            return;
        }

        boolean canEnter = SpectatorStateHelper.isInGameRealSpectator(player, gameWorld);
        if (!inArena && !canEnter) {
            return;
        }

        Text line = Text.translatable(
                inArena ? "tip.noellesroles.death_arena.exit" : "tip.noellesroles.death_arena.enter",
                NoellesrolesClient.deathArenaToggleBind.getBoundKeyLocalizedText()
        );

        HudRenderHelper.pushAboveVoiceChatHudLayer(context);
        try {
            int drawY = TopCenterHudAnchor.getTopY();
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    line,
                    context.getScaledWindowWidth() / 2,
                    drawY,
                    0xFFE6E6E6
            );
        } finally {
            HudRenderHelper.popAboveVoiceChatHudLayer(context);
        }
    }
}
