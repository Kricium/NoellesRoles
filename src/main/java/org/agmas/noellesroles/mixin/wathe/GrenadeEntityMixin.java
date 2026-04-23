package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.entity.GrenadeEntity;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import org.agmas.noellesroles.hallucination.HallucinationDummyInteractionHelper;
import org.agmas.noellesroles.util.AreaDamageImmunityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrenadeEntity.class)
public class GrenadeEntityMixin {

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void noellesroles$killHallucinationDummies(HitResult hitResult, CallbackInfo ci) {
        GrenadeEntity grenade = (GrenadeEntity) (Object) this;
        Entity owner = grenade.getOwner();
        if (!(owner instanceof ServerPlayerEntity viewer)) {
            return;
        }
        Box explosionBox = grenade.getBoundingBox().expand(3.0D);
        HallucinationDummyInteractionHelper.killDummiesInBox(
                viewer,
                explosionBox,
                GameConstants.DeathReasons.GRENADE,
                true,
                null,
                null,
                null
        );
    }

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
