package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.ferryman.FerrymanPlayerComponent;

public final class FerrymanHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        FerrymanPlayerComponent ferryman = FerrymanPlayerComponent.KEY.get(player);
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);

        Text line;
        if (ferryman.isReactionActive()) {
            line = Text.translatable("tip.ferryman.reaction_ready", HudRenderHelper.getAbilityKeyText());
        } else if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        } else if (NoellesrolesClient.targetBody != null) {
            line = Text.translatable("tip.ferryman.ferry", HudRenderHelper.getAbilityKeyText());
        } else {
            line = Text.translatable("tip.ferryman.progress", ferryman.getFerriedCount(), ferryman.getFerriedRequired(), ferryman.getBlessingStacks());
        }

        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
