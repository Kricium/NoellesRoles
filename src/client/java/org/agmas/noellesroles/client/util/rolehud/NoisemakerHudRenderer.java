package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.noisemaker.NoisemakerPlayerComponent;

public final class NoisemakerHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        NoisemakerPlayerComponent noisemakerComp = NoisemakerPlayerComponent.KEY.get(player);

        Text line = Text.translatable("tip.noisemaker", HudRenderHelper.getAbilityKeyText());
        if (noisemakerComp.isBroadcasting()) {
            line = Text.translatable("tip.noisemaker.active", noisemakerComp.getBroadcastTicksRemaining() / 20);
        } else if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        }

        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
