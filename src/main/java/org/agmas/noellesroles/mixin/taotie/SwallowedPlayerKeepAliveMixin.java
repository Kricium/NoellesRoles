package org.agmas.noellesroles.mixin.taotie;

import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.noellesroles.taotie.SwallowedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class SwallowedPlayerKeepAliveMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void noellesroles$keepSwallowedPlayerStable(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (!SwallowedPlayerComponent.isPlayerSwallowed(player)) {
            return;
        }

        player.setOnGround(true);
        player.fallDistance = 0.0F;
        player.setVelocity(0.0, 0.0, 0.0);
    }
}
