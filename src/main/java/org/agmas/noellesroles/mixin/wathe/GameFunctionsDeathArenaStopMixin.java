package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.server.world.ServerWorld;
import org.agmas.noellesroles.deatharena.DeathArenaServerController;
import org.agmas.noellesroles.deatharena.DeathArenaStateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class GameFunctionsDeathArenaStopMixin {
    @Inject(method = "stopGame", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$redirectArenaStopToOriginWorld(ServerWorld world, CallbackInfo ci) {
        ServerWorld targetWorld = DeathArenaStateHelper.resolveGameControlWorld(world);
        if (targetWorld != null && targetWorld != world) {
            GameFunctions.stopGame(targetWorld);
            ci.cancel();
            return;
        }

        if (!DeathArenaStateHelper.isDeathArenaDimension(world)) {
            return;
        }

        DeathArenaServerController.forceShutdownFromAnyContext(world, true);
        ci.cancel();
    }

    @Inject(method = "finalizeGame", at = @At("HEAD"), cancellable = true)
    private static void noellesroles$redirectArenaFinalizeToOriginWorld(ServerWorld world, CallbackInfo ci) {
        ServerWorld targetWorld = DeathArenaStateHelper.resolveGameControlWorld(world);
        if (targetWorld != null && targetWorld != world) {
            GameFunctions.finalizeGame(targetWorld);
            ci.cancel();
            return;
        }

        if (!DeathArenaStateHelper.isDeathArenaDimension(world)) {
            return;
        }

        DeathArenaServerController.forceShutdownFromAnyContext(world, true);
        ci.cancel();
    }
}
