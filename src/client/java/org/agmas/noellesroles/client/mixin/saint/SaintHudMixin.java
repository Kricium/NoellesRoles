package org.agmas.noellesroles.client.mixin.saint;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.saint.SaintPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class SaintHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("TAIL"))
    private void noellesroles$renderSaintHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ClientPlayerEntity player = HudRenderHelper.getActivePlayer();
        if (player == null) return;

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        SaintPlayerComponent saintComponent = SaintPlayerComponent.KEY.get(player);
        Text line = null;

        if (gameWorld.isRole(player, Noellesroles.SAINT)) {
            if (saintComponent.isHellfireActive()) {
                line = Text.translatable("tip.saint.active", Math.max(1, saintComponent.getHellfireActiveTicks() / 20));
            } else {
                AbilityPlayerComponent abilityComponent = AbilityPlayerComponent.KEY.get(player);
                if (abilityComponent.getCooldown() > 0) {
                    line = Text.translatable("tip.noellesroles.cooldown", Math.max(1, abilityComponent.getCooldown() / 20));
                } else {
                    line = Text.translatable("tip.saint.ready", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText());
                }
            }
        } else if (saintComponent.isKarmaLocked()) {
            line = Text.translatable("tip.saint.karma_locked", Math.max(1, saintComponent.getKarmaLockTicks() / 20));
        }

        if (line == null) return;
        int lineHeight = getTextRenderer().getWrappedLinesHeight(line, 999999);
        int skillHudTopY = HudRenderHelper.getBottomRightSkillHudTopY(context, getTextRenderer(), player);
        int drawY = skillHudTopY < context.getScaledWindowHeight()
                ? skillHudTopY - lineHeight - 2
                : context.getScaledWindowHeight() - lineHeight;
        int drawX = context.getScaledWindowWidth()
                - getTextRenderer().getWidth(line)
                - HudRenderHelper.getBottomRightSkillHudRightPadding(player);

        context.drawTextWithShadow(getTextRenderer(), line, drawX, drawY, 0xF29A64);
    }
}
