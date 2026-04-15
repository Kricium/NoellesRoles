package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;

public final class SwapperHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        Text line = ability.getCooldown() > 0
                ? Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20)
                : Text.translatable("tip.swapper.ready", HudRenderHelper.getAbilityKeyText());
        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
