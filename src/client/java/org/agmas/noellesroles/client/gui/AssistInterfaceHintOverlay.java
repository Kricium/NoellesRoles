package org.agmas.noellesroles.client.gui;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;

public final class AssistInterfaceHintOverlay {
    private static boolean renderedViaVoiceChatThisFrame;

    private AssistInterfaceHintOverlay() {
    }

    public static void beginHudFrame() {
        renderedViaVoiceChatThisFrame = false;
    }

    public static void markVoiceChatRendered() {
        renderedViaVoiceChatThisFrame = true;
    }

    public static boolean wasRenderedViaVoiceChatThisFrame() {
        return renderedViaVoiceChatThisFrame;
    }

    public static void render(DrawContext context, TextRenderer textRenderer) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.currentScreen != null) return;

        GameWorldComponent gwc = GameWorldComponent.KEY.get(mc.player.getWorld());
        if (NoellesrolesClient.assistInterfaceBind == null) return;

        boolean isSwallowed = SwallowedPlayerComponent.isPlayerSwallowed(mc.player);
        boolean canOpenRoleInfo = gwc.hasAnyRole(mc.player) && (GameFunctions.isPlayerPlayingAndAlive(mc.player) || isSwallowed);
        boolean isInGameSpectator = mc.player.isSpectator() && gwc.isRunning() && !isSwallowed;
        boolean canOpenSpectatorPanel = !canOpenRoleInfo && isInGameSpectator;
        if (!canOpenRoleInfo && !canOpenSpectatorPanel) return;

        String keyName = NoellesrolesClient.assistInterfaceBind.getBoundKeyLocalizedText().getString();
        Text hintText = canOpenSpectatorPanel
                ? Text.translatable("assist_interface.spectator_hint", keyName)
                : Text.translatable("assist_interface.hint", keyName);

        int drawY = context.getScaledWindowHeight() - textRenderer.getWrappedLinesHeight(hintText, 999999) - 5;
        int x = 5;

        float time = (System.currentTimeMillis() % 3000) / 3000f;
        float alpha = (float) (Math.sin(time * Math.PI * 2) * 0.12 + 0.45);
        int alphaInt = (int) (alpha * 255);
        int color = (alphaInt << 24) | 0xCCCCCC;

        HudRenderHelper.pushAboveVoiceChatHudLayer(context);
        context.drawTextWithShadow(textRenderer, hintText, x, drawY, color);
        HudRenderHelper.popAboveVoiceChatHudLayer(context);
    }
}
