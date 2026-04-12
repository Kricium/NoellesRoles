package org.agmas.noellesroles.client.util.rolehud;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.criminalreasoner.CriminalReasonerPlayerComponent;

public final class CriminalReasonerHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        CriminalReasonerPlayerComponent comp = CriminalReasonerPlayerComponent.KEY.get(player);
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);

        Text cooldownText = abilityComp.getCooldown() > 0
                ? Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20)
                : Text.translatable("hud.criminal_reasoner.press_key_hint", HudRenderHelper.getAbilityKeyName());

        int requiredReasoningCount = Math.floorDiv(GameWorldComponent.KEY.get(player.getWorld()).getAllPlayers().size(), 3);
        Text progressText = Text.translatable(
                "hud.criminal_reasoner.progress",
                comp.getSuccessfulReasoningCount(),
                requiredReasoningCount
        );

        int drawY = HudRenderHelper.stackLine(bottom, renderer, cooldownText, 0);
        return HudRenderHelper.stackLine(drawY, renderer, progressText, 0);
    }
}
