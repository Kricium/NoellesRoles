package org.agmas.noellesroles.mixin.wathe;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRecordManager.class)
public class GameRecordManagerDeathMixin {

    @Inject(
            method = "recordDeath",
            at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/record/GameRecordManager;addEvent(Lnet/minecraft/server/world/ServerWorld;Ljava/lang/String;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/nbt/NbtCompound;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1
    )
    private static void noellesroles$attachDeathPosition(
            ServerPlayerEntity victim,
            ServerPlayerEntity killer,
            Identifier deathReason,
            CallbackInfo ci,
            NbtCompound data) {
        GameRecordManager.putPos(data, "death_pos", victim.getPos());
    }
}
