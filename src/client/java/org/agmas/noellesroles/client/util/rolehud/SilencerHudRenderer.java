package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.silencer.SilencerPlayerComponent;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;

public final class SilencerHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        if (SwallowedPlayerComponent.isPlayerSwallowed(player)) return bottom;

        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        SilencerPlayerComponent silencerComp = SilencerPlayerComponent.KEY.get(player);
        Text line;

        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        } else if (silencerComp.hasMarkedTarget()) {
            line = Text.translatable(
                    "tip.silencer.confirm",
                    HudRenderHelper.getAbilityKeyName(),
                    silencerComp.getMarkedTargetName(),
                    silencerComp.getMarkTicksRemaining() / 20);
        } else if (NoellesrolesClient.crosshairTarget != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
            line = Text.translatable(
                    "tip.silencer.mark",
                    HudRenderHelper.getAbilityKeyName(),
                    NoellesrolesClient.crosshairTarget.getName().getString());
        } else {
            line = Text.translatable("tip.silencer.ready", HudRenderHelper.getAbilityKeyName());
        }
        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
