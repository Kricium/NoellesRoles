package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameRoundEndComponent;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.survivalmaster.SurvivalMasterPlayerComponent;
import org.agmas.noellesroles.util.SurvivalMasterRoundEndAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRoundEndComponent.class)
public class GameRoundEndComponentMixin implements SurvivalMasterRoundEndAccess {
    @Unique
    private boolean noellesroles$survivalMasterMomentWin = false;

    @Redirect(
            method = "didWin(Ljava/util/UUID;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/api/WatheRoles;getRole(Lnet/minecraft/util/Identifier;)Ldev/doctor4t/wathe/api/Role;"
            )
    )
    private Role noellesroles$guardMissingRoundEndRole(Identifier identifier) {
        Role role = WatheRoles.getRole(identifier);
        return role != null ? role : WatheRoles.NO_ROLE;
    }

    @Inject(
            method = "setRoundEndData(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/game/GameFunctions$WinStatus;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/cca/GameRoundEndComponent;sync()V"
            )
    )
    private void noellesroles$captureSurvivalMasterMomentWin(ServerWorld world, GameFunctions.WinStatus winStatus, CallbackInfo ci) {
        this.noellesroles$survivalMasterMomentWin = false;
        if (winStatus != GameFunctions.WinStatus.PASSENGERS) {
            return;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(world);
        for (var uuid : gameWorld.getAllWithRole(Noellesroles.SURVIVAL_MASTER)) {
            PlayerEntity survivalMaster = world.getPlayerByUuid(uuid);
            if (!GameFunctions.isPlayerPlayingAndAlive(survivalMaster)) {
                continue;
            }
            if (SurvivalMasterPlayerComponent.KEY.get(survivalMaster).hasSurvivalMomentCompleted()) {
                this.noellesroles$survivalMasterMomentWin = true;
                return;
            }
        }
    }

    @Inject(
            method = "setRoundEndData(Lnet/minecraft/server/world/ServerWorld;Ljava/util/UUID;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/wathe/cca/GameRoundEndComponent;sync()V"
            )
    )
    private void noellesroles$clearSurvivalMasterMomentWin(ServerWorld world, java.util.UUID winnerUuid, CallbackInfo ci) {
        this.noellesroles$survivalMasterMomentWin = false;
    }

    @Inject(method = "writeToNbt", at = @At("TAIL"))
    private void noellesroles$writeSurvivalMasterMomentWin(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        tag.putBoolean("noellesroles$survivalMasterMomentWin", this.noellesroles$survivalMasterMomentWin);
    }

    @Inject(method = "readFromNbt", at = @At("TAIL"))
    private void noellesroles$readSurvivalMasterMomentWin(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
        this.noellesroles$survivalMasterMomentWin = tag.getBoolean("noellesroles$survivalMasterMomentWin");
    }

    @Override
    public boolean noellesroles$isSurvivalMasterMomentWin() {
        return this.noellesroles$survivalMasterMomentWin;
    }
}
