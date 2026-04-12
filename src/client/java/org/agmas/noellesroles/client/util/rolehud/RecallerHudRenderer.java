package org.agmas.noellesroles.client.util.rolehud;

import dev.doctor4t.wathe.cca.PlayerShopComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.recaller.RecallerPlayerComponent;

public final class RecallerHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
        RecallerPlayerComponent recaller = RecallerPlayerComponent.KEY.get(player);
        PlayerShopComponent shop = PlayerShopComponent.KEY.get(player);

        Text line = Text.translatable("tip.recaller.teleport", HudRenderHelper.getAbilityKeyText());
        if (!recaller.placed) {
            line = Text.translatable("tip.recaller.place", HudRenderHelper.getAbilityKeyText());
        } else if (shop.balance < 100) {
            line = Text.translatable("tip.recaller.not_enough_money");
        }

        if (ability.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", ability.getCooldown() / 20);
        }

        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
