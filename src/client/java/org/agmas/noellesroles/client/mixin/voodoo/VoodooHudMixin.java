package org.agmas.noellesroles.client.mixin.voodoo;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public abstract class VoodooHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void noellesroles$renderVoodooHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!GameFunctions.isPlayerPlayingAndAlive(MinecraftClient.getInstance().player)) return;

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (!gameWorld.isRole(MinecraftClient.getInstance().player, Noellesroles.VOODOO)) return;

        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        VoodooPlayerComponent voodoo = VoodooPlayerComponent.KEY.get(MinecraftClient.getInstance().player);
        int drawY = context.getScaledWindowHeight();

        HudRenderHelper.pushAboveVoiceChatHudLayer(context);
        try {
            UUID targetUuid = voodoo.target;
            if (targetUuid != null && !targetUuid.equals(MinecraftClient.getInstance().player.getUuid())) {
                PlayerEntity target = MinecraftClient.getInstance().player.getWorld().getPlayerByUuid(targetUuid);
                if (target != null) {
                    Text line = Text.translatable("tip.voodoo.bound", target.getName());
                    drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
                    context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, Noellesroles.VOODOO.color());
                    drawY -= 2;
                }
            }

            Text line = ability.getCooldown() > 0
                    ? Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20)
                    : Text.translatable("tip.voodoo.ready", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText());
            drawY -= getTextRenderer().getWrappedLinesHeight(line, 999999);
            context.drawTextWithShadow(getTextRenderer(), line, context.getScaledWindowWidth() - getTextRenderer().getWidth(line), drawY, Noellesroles.VOODOO.color());
        } finally {
            HudRenderHelper.popAboveVoiceChatHudLayer(context);
        }
    }
}
