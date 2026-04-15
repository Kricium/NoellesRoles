package org.agmas.noellesroles.client.mixin.roleinfo;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.gui.SpectatorReplayToastOverlay;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * HUD mixin that shows the assist-interface hint above the hotbar
 * when the player is alive and has a role assigned during an active game.
 */
@Mixin(InGameHud.class)
public abstract class RoleInfoHudMixin {
    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    public void renderAssistInterfaceHint(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        SpectatorReplayToastOverlay.render(context, getTextRenderer());
        if (mc.player == null) return;
        // Don't show hint when a screen is open
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

        int drawY = context.getScaledWindowHeight();
        drawY -= getTextRenderer().getWrappedLinesHeight(hintText, 999999);
        drawY -= 5;
        int x = 5;

        // Subtle breathing alpha effect
        float time = (System.currentTimeMillis() % 3000) / 3000f;
        float alpha = (float) (Math.sin(time * Math.PI * 2) * 0.12 + 0.45);
        int alphaInt = (int) (alpha * 255);
        int color = (alphaInt << 24) | 0xCCCCCC;

        context.drawTextWithShadow(getTextRenderer(), hintText, x, drawY, color);
    }
}
