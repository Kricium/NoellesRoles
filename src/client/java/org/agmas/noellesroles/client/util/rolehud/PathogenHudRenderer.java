package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;

public final class PathogenHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        Text line = null;

        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        }

        if (NoellesrolesClient.pathogenNearestTarget != null) {
            double distanceSquared = player.squaredDistanceTo(NoellesrolesClient.pathogenNearestTarget);
            boolean canInfect = distanceSquared < 9.0 && player.canSee(NoellesrolesClient.pathogenNearestTarget);
            if (canInfect && abilityComp.getCooldown() <= 0) {
                line = Text.translatable(
                        "tip.pathogen.infect",
                        HudRenderHelper.getAbilityKeyName(),
                        NoellesrolesClient.pathogenNearestTarget.getName().getString());
            }
        }

        return line == null ? bottom : HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
