package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.agmas.noellesroles.taotie.TaotiePlayerComponent;

public final class TaotieHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        TaotiePlayerComponent comp = TaotiePlayerComponent.KEY.get(player);
        int drawY = bottom;

        if (comp.isTaotieMomentActive()) {
            drawY = HudRenderHelper.stackLine(drawY, renderer,
                    Text.translatable("tip.taotie.moment_active", comp.getTaotieMomentTicks() / 20), HudRenderHelper.LINE_GAP);
        }
        if (comp.getSwallowedCount() > 0) {
            drawY = HudRenderHelper.stackLine(drawY, renderer,
                    Text.translatable("tip.taotie.swallowed_count", comp.getSwallowedCount()), HudRenderHelper.LINE_GAP);
        }
        if (comp.getSwallowCooldown() > 0) {
            drawY = HudRenderHelper.stackLine(drawY, renderer,
                    Text.translatable("tip.noellesroles.cooldown", comp.getSwallowCooldown() / 20), HudRenderHelper.LINE_GAP);
        }

        if (NoellesrolesClient.crosshairTarget != null && NoellesrolesClient.crosshairTargetDistance <= 3.0) {
            SwallowedPlayerComponent swallowed = SwallowedPlayerComponent.KEY.get(NoellesrolesClient.crosshairTarget);
            if (!swallowed.isSwallowed() && comp.getSwallowCooldown() <= 0) {
                drawY = HudRenderHelper.stackLine(drawY, renderer,
                        Text.translatable("tip.taotie.swallow",
                                HudRenderHelper.getAbilityKeyName(),
                                NoellesrolesClient.crosshairTarget.getName().getString()),
                        0);
            }
        }
        return drawY;
    }
}
