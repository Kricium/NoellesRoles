package org.agmas.noellesroles.client.util;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.saint.SaintPlayerComponent;
import org.jetbrains.annotations.Nullable;

public final class SaintHudHelper {
    private SaintHudHelper() {}

    @Nullable
    public static Text getHudLine(ClientPlayerEntity player) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(player.getWorld());
        SaintPlayerComponent saint = SaintPlayerComponent.KEY.get(player);

        if (gameWorld.isRole(player, Noellesroles.SAINT)) {
            if (saint.isHellfireActive()) {
                return Text.translatable("tip.saint.active",
                        Math.max(1, saint.getHellfireActiveTicks() / 20));
            }
            AbilityPlayerComponent ability = AbilityPlayerComponent.KEY.get(player);
            if (ability.getCooldown() > 0) {
                return Text.translatable("tip.noellesroles.cooldown",
                        Math.max(1, ability.getCooldown() / 20));
            }
            return Text.translatable("tip.saint.ready",
                    NoellesrolesClient.abilityBind.getBoundKeyLocalizedText());
        }

        if (saint.isKarmaLocked()) {
            return Text.translatable("tip.saint.karma_locked",
                    Math.max(1, saint.getKarmaLockTicks() / 20));
        }
        return null;
    }
}
