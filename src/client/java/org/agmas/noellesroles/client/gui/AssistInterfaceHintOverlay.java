package org.agmas.noellesroles.client.gui;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.agmas.noellesroles.util.SpectatorStateHelper;

public final class AssistInterfaceHintOverlay {
    private static final int SCREEN_MARGIN = 5;
    private static final int STACK_SPACING = 2;
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
        Text roleInfoHint = getRoleInfoHintText(mc, gwc);
        Text spectatorHint = getSpectatorHintText(mc, gwc);
        Text configHint = getConfigHintText(mc, gwc);
        if (roleInfoHint == null && spectatorHint == null && configHint == null) return;

        int color = getPulseColor();
        HudRenderHelper.pushAboveVoiceChatHudLayer(context);
        if (gwc.isRunning()) {
            renderInGameHints(context, textRenderer, roleInfoHint, spectatorHint, configHint, color);
        } else {
            renderLobbyHints(context, textRenderer, roleInfoHint, configHint, color);
        }
        HudRenderHelper.popAboveVoiceChatHudLayer(context);
    }

    private static void renderInGameHints(DrawContext context, TextRenderer textRenderer, Text roleInfoHint, Text spectatorHint, Text configHint, int color) {
        int drawBaseY = context.getScaledWindowHeight() - SCREEN_MARGIN;
        if (configHint != null) {
            int configHeight = textRenderer.getWrappedLinesHeight(configHint, Integer.MAX_VALUE);
            int configDrawY = drawBaseY - configHeight;
            context.drawTextWithShadow(textRenderer, configHint, SCREEN_MARGIN, configDrawY, color);
            drawBaseY = configDrawY - STACK_SPACING;
        }

        if (roleInfoHint != null) {
            int roleInfoHeight = textRenderer.getWrappedLinesHeight(roleInfoHint, Integer.MAX_VALUE);
            int roleInfoDrawY = drawBaseY - roleInfoHeight;
            context.drawTextWithShadow(textRenderer, roleInfoHint, SCREEN_MARGIN, roleInfoDrawY, color);
            drawBaseY = roleInfoDrawY - STACK_SPACING;
        }

        if (spectatorHint != null) {
            int spectatorHeight = textRenderer.getWrappedLinesHeight(spectatorHint, Integer.MAX_VALUE);
            int spectatorDrawY = drawBaseY - spectatorHeight;
            context.drawTextWithShadow(textRenderer, spectatorHint, SCREEN_MARGIN, spectatorDrawY, color);
        }
    }

    private static void renderLobbyHints(DrawContext context, TextRenderer textRenderer, Text roleInfoHint, Text configHint, int color) {
        int rightEdge = context.getScaledWindowWidth() - SCREEN_MARGIN;
        int drawY = context.getScaledWindowHeight() - SCREEN_MARGIN;

        if (configHint != null) {
            drawY -= textRenderer.getWrappedLinesHeight(configHint, Integer.MAX_VALUE);
            context.drawTextWithShadow(textRenderer, configHint, rightEdge - textRenderer.getWidth(configHint), drawY, color);
            drawY -= STACK_SPACING;
        }
        if (roleInfoHint != null) {
            drawY -= textRenderer.getWrappedLinesHeight(roleInfoHint, Integer.MAX_VALUE);
            context.drawTextWithShadow(textRenderer, roleInfoHint, rightEdge - textRenderer.getWidth(roleInfoHint), drawY, color);
        }
    }

    private static Text getRoleInfoHintText(MinecraftClient mc, GameWorldComponent gwc) {
        if (!NoellesRolesConfig.HANDLER.instance().showAssistInterfaceHint) {
            return null;
        }
        if (NoellesrolesClient.roleInfoBind == null) {
            return null;
        }

        if (!gwc.isRunning()) {
            String keyName = NoellesrolesClient.roleInfoBind.getBoundKeyLocalizedText().getString();
            return Text.translatable("role_info.hint", keyName);
        }

        boolean isSwallowed = SwallowedPlayerComponent.isPlayerSwallowed(mc.player);
        boolean canOpenRoleInfo = gwc.hasAnyRole(mc.player) && (GameFunctions.isPlayerPlayingAndAlive(mc.player) || isSwallowed);
        boolean isInGameSpectator = SpectatorStateHelper.isInGameRealSpectator(mc.player, gwc);
        if (!canOpenRoleInfo && !isInGameSpectator) {
            return null;
        }

        String keyName = NoellesrolesClient.roleInfoBind.getBoundKeyLocalizedText().getString();
        return Text.translatable("role_info.hint", keyName);
    }

    private static Text getSpectatorHintText(MinecraftClient mc, GameWorldComponent gwc) {
        if (!NoellesRolesConfig.HANDLER.instance().showAssistInterfaceHint) {
            return null;
        }
        if (NoellesrolesClient.assistInterfaceBind == null) {
            return null;
        }
        if (!gwc.isRunning()) {
            return null;
        }
        if (!SpectatorStateHelper.isInGameRealSpectator(mc.player, gwc)) {
            return null;
        }

        String keyName = NoellesrolesClient.assistInterfaceBind.getBoundKeyLocalizedText().getString();
        return Text.translatable("assist_interface.spectator_hint", keyName);
    }

    private static Text getConfigHintText(MinecraftClient mc, GameWorldComponent gwc) {
        if (!NoellesRolesConfig.HANDLER.instance().showConfigScreenHint) {
            return null;
        }
        if (NoellesrolesClient.configScreenBind == null) {
            return null;
        }
        if (mc.currentScreen != null) {
            return null;
        }
        if (mc.player == null) {
            return null;
        }

        String keyName = NoellesrolesClient.configScreenBind.getBoundKeyLocalizedText().getString();
        return gwc.isRunning()
                ? Text.translatable("config_screen.hint_ingame", keyName)
                : Text.translatable("config_screen.hint_lobby", keyName);
    }

    private static int getPulseColor() {
        float time = (System.currentTimeMillis() % 3000) / 3000f;
        float alpha = (float) (Math.sin(time * Math.PI * 2) * 0.12 + 0.45);
        int alphaInt = (int) (alpha * 255);
        return (alphaInt << 24) | 0xCCCCCC;
    }
}
