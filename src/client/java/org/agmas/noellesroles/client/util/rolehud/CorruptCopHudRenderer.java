package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.corruptcop.CorruptCopPlayerComponent;

public final class CorruptCopHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        CorruptCopPlayerComponent comp = CorruptCopPlayerComponent.KEY.get(player);
        if (!comp.isCorruptCopMomentActive()) return bottom;

        int visionCycleTimer = comp.getVisionCycleTimer();
        Text line = comp.canSeePlayersThroughWalls()
                ? Text.translatable("tip.corrupt_cop.vision_active", (30 * 20 - visionCycleTimer) / 20)
                : Text.translatable("tip.corrupt_cop.vision_inactive", (20 * 20 - visionCycleTimer) / 20);
        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
