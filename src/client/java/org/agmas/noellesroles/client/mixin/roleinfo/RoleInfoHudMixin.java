package org.agmas.noellesroles.client.mixin.roleinfo;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.agmas.noellesroles.client.gui.AssistInterfaceHintOverlay;
import org.agmas.noellesroles.client.gui.SpectatorReplayToastOverlay;
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

    @Inject(method = "render", at = @At("HEAD"))
    public void noellesroles$beginAssistOverlayFrame(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        AssistInterfaceHintOverlay.beginHudFrame();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderAssistOverlays(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (AssistInterfaceHintOverlay.wasRenderedViaVoiceChatThisFrame()) {
            return;
        }
        SpectatorReplayToastOverlay.render(context, getTextRenderer());
        AssistInterfaceHintOverlay.render(context, getTextRenderer());
    }
}
