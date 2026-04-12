package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.assassin.AssassinPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;

public final class AssassinHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AssassinPlayerComponent comp = AssassinPlayerComponent.KEY.get(player);
        int drawY = bottom - HudRenderHelper.ASSASSIN_BOTTOM_PADDING;

        drawY = HudRenderHelper.stackLine(drawY, renderer,
                Text.translatable("hud.assassin.guesses_remaining", comp.getGuessesRemaining(), comp.getMaxGuesses()),
                0);

        if (comp.getCooldownTicks() > 0) {
            int cooldownSeconds = (comp.getCooldownTicks() + 19) / 20;
            drawY = HudRenderHelper.stackLine(drawY, renderer, Text.translatable("hud.assassin.cooldown", cooldownSeconds), HudRenderHelper.LINE_GAP);
        }

        if (comp.canGuess()) {
            drawY = HudRenderHelper.stackLine(drawY, renderer,
                    Text.translatable("hud.assassin.press_key_hint", HudRenderHelper.getAbilityKeyName()), HudRenderHelper.LINE_GAP);
        } else if (comp.getCooldownTicks() > 0) {
            drawY = HudRenderHelper.stackLine(drawY, renderer, Text.translatable("hud.assassin.on_cooldown"), HudRenderHelper.LINE_GAP);
        } else if (comp.getGuessesRemaining() <= 0) {
            drawY = HudRenderHelper.stackLine(drawY, renderer, Text.translatable("hud.assassin.no_guesses"), HudRenderHelper.LINE_GAP);
        }

        return drawY;
    }
}
