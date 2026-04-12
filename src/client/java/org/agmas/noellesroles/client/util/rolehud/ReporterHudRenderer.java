package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.reporter.ReporterPlayerComponent;

public final class ReporterHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        ReporterPlayerComponent reporter = ReporterPlayerComponent.KEY.get(player);
        int drawY = bottom;

        if (reporter.hasMarkedTarget()) {
            PlayerEntity markedTarget = player.getWorld().getPlayerByUuid(reporter.getMarkedTarget());
            if (markedTarget != null) {
                Text line2 = Text.translatable("tip.reporter.marked", markedTarget.getName());
                drawY = HudRenderHelper.stackLine(drawY, renderer, line2, 0);
            }
        }

        Text line1;
        if (ability.getCooldown() > 0) {
            line1 = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        } else {
            PlayerEntity target = NoellesrolesClient.crosshairTarget;
            if (target != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
                line1 = Text.translatable("tip.reporter.target", target.getName(), HudRenderHelper.getAbilityKeyText());
            } else {
                line1 = Text.translatable("tip.reporter.no_target");
            }
        }

        return HudRenderHelper.stackLine(drawY, renderer, line1, 0);
    }
}
