package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;

public final class PhantomHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        Text line = Text.translatable("tip.phantom", HudRenderHelper.getAbilityKeyText());

        var invisEffect = player.getStatusEffect(StatusEffects.INVISIBILITY);
        if (invisEffect != null) {
            line = Text.translatable("tip.phantom.active", invisEffect.getDuration() / 20);
        } else if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        }

        return HudRenderHelper.stackLine(bottom, renderer, line, 0);
    }
}
