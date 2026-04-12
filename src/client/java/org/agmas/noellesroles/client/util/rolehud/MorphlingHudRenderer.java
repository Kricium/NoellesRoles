package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;

public final class MorphlingHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        MorphlingPlayerComponent comp = MorphlingPlayerComponent.KEY.get(player);

        int morphTicks = comp.getMorphTicks();
        Text statusLine;
        if (morphTicks > 0) {
            statusLine = Text.translatable("tip.morphling.active", morphTicks / 20);
        } else if (morphTicks < 0) {
            statusLine = Text.translatable("tip.noellesroles.cooldown", (-morphTicks) / 20);
        } else {
            statusLine = Text.translatable("tip.morphling");
        }
        int drawY = HudRenderHelper.stackLine(bottom, renderer, statusLine, 0);

        Text corpseHint = Text.translatable(
                comp.corpseMode ? "tip.morphling.corpse_active" : "tip.morphling.corpse_hint",
                HudRenderHelper.getAbilityKeyText());
        return HudRenderHelper.stackLine(drawY, renderer, corpseHint, 0);
    }
}
