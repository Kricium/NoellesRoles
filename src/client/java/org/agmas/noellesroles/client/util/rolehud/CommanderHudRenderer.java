package org.agmas.noellesroles.client.util.rolehud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.client.util.HudRenderHelper;
import org.agmas.noellesroles.commander.CommanderPlayerComponent;

public final class CommanderHudRenderer implements RoleHudRenderer {
    @Override
    public int getTopY(TextRenderer renderer, ClientPlayerEntity player, int bottom) {
        CommanderPlayerComponent commanderComp = CommanderPlayerComponent.KEY.get(player);
        AbilityPlayerComponent abilityComp = AbilityPlayerComponent.KEY.get(player);
        int drawY = bottom;

        if (!commanderComp.getThreatTargetNames().isEmpty()) {
            drawY = HudRenderHelper.stackLine(drawY, renderer,
                    Text.translatable("tip.commander.marked", String.join(", ", commanderComp.getThreatTargetNames())),
                    0);
        }

        Text line;
        if (abilityComp.getCooldown() > 0) {
            line = Text.translatable("tip.noellesroles.cooldown", abilityComp.getCooldown() / 20);
        } else if (commanderComp.canMarkMore()) {
            line = Text.translatable("tip.commander.ready", HudRenderHelper.getAbilityKeyText(), commanderComp.getRemainingMarks());
        } else {
            line = Text.translatable("tip.commander.no_marks");
        }
        return HudRenderHelper.stackLine(drawY, renderer, line, 0);
    }
}
