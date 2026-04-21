package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.game.gamemode.LooseEndsGameMode;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.deatharena.DeathArenaStateHelper;
import org.agmas.noellesroles.looseend.LooseEndsRadarHelper;
import org.agmas.noellesroles.looseend.LooseEndsRadarWorldComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LooseEndsGameMode.class)
public abstract class LooseEndsGameModeMixin {

    @Redirect(
            method = "initializeGame",
            at = @At(
                    value = "FIELD",
                    target = "Ldev/doctor4t/wathe/index/WatheItems;CROWBAR:Lnet/minecraft/item/Item;"
            )
    )
    private Item noellesroles$replaceInitialCrowbarWithMasterKey() {
        return ModItems.MASTER_KEY;
    }

    @Inject(method = "initializeGame", at = @At("TAIL"))
    private void noellesroles$initializeRadarLoop(ServerWorld world, GameWorldComponent gameComponent, List<ServerPlayerEntity> players, CallbackInfo ci) {
        for (ServerPlayerEntity player : players) {
            noellesroles$removeInitialDerringer(player);
        }
        LooseEndsRadarWorldComponent.KEY.get(world).startRound();
    }

    @Inject(method = "tickServerGameLoop", at = @At("HEAD"), cancellable = true)
    private void noellesroles$cancelDeathArenaWinLoop(ServerWorld world, GameWorldComponent gameComponent, CallbackInfo ci) {
        if (!DeathArenaStateHelper.isDeathArenaDimension(world)) {
            return;
        }

        noellesroles$tickRadarLoopInternal(world, gameComponent);
        ci.cancel();
    }

    @Inject(method = "tickServerGameLoop", at = @At("TAIL"))
    private void noellesroles$tickRadarLoop(ServerWorld world, GameWorldComponent gameComponent, CallbackInfo ci) {
        if (DeathArenaStateHelper.isDeathArenaDimension(world)) {
            return;
        }

        noellesroles$tickRadarLoopInternal(world, gameComponent);
    }

    private static void noellesroles$removeInitialDerringer(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(WatheItems.DERRINGER)) {
                player.getInventory().setStack(i, ItemStack.EMPTY);
            }
        }
    }

    private static void noellesroles$tickRadarLoopInternal(ServerWorld world, GameWorldComponent gameComponent) {
        if (gameComponent.getGameStatus() != GameWorldComponent.GameStatus.ACTIVE) {
            return;
        }

        LooseEndsRadarWorldComponent radarComponent = LooseEndsRadarWorldComponent.KEY.get(world);
        if (radarComponent.isInactive()) {
            return;
        }

        int ticksRemaining = radarComponent.tick();

        if (radarComponent.isCountdown()) {
            if (ticksRemaining <= 0) {
                radarComponent.startScan();
                LooseEndsRadarHelper.applyRadarGlow(world, null);
                LooseEndsRadarWorldComponent.KEY.sync(world);
                return;
            }

            if (ticksRemaining % 20 == 0) {
                LooseEndsRadarWorldComponent.KEY.sync(world);
            }
            return;
        }

        if (radarComponent.isScanning()) {
            if (ticksRemaining <= 0) {
                radarComponent.startCountdown();
                LooseEndsRadarWorldComponent.KEY.sync(world);
                return;
            }

            if (ticksRemaining % 20 == 0) {
                LooseEndsRadarWorldComponent.KEY.sync(world);
            }
        }
    }
}
