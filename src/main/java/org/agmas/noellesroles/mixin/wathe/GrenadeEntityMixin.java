package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.entity.GrenadeEntity;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.noellesroles.util.AreaDamageImmunityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GrenadeEntity.class)
public class GrenadeEntityMixin {

    @Redirect(
            method = "onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/game/GameFunctions;killPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;ZLnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/Identifier;)V"
            )
    )
    private void noellesroles$skipAreaDamageImmunePlayers(
            ServerPlayerEntity victim,
            boolean deadBody,
            ServerPlayerEntity killer,
            net.minecraft.util.Identifier deathReason
    ) {
        if (AreaDamageImmunityHelper.isImmuneToAreaDamage(victim)) {
            return;
        }
        GameFunctions.killPlayer(victim, deadBody, killer, deathReason);
    }
}
