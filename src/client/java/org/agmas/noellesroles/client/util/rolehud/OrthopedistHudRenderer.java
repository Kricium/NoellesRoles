package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;

public final class OrthopedistHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        Text line;

        if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        } else {
            PlayerEntity target = NoellesrolesClient.crosshairTarget;
            if (target != null && NoellesrolesClient.crosshairTargetDistance <= 3.0 && player.canSee(target)) {
                line = Text.translatable("tip.orthopedist.buff", HudRenderHelper.getAbilityKeyText(), target.getName());
            } else {
                line = Text.translatable("tip.orthopedist.no_target");
            }
        }

        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
