package org.agmas.noellesroles.client.mixin.silencer;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.agmas.noellesroles.client.silencer.SilencerBatClientSoundGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class SilencerBatClientWorldSoundMixin {

    @Inject(
            method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noellesroles$silenceDirectClientSound(
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundCategory category,
            float volume,
            float pitch,
            boolean useDistance,
            CallbackInfo ci
    ) {
        if (SilencerBatClientSoundGate.shouldSuppress(sound, category)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noellesroles$silencePacketClientSound(
            net.minecraft.entity.player.PlayerEntity except,
            double x,
            double y,
            double z,
            RegistryEntry<SoundEvent> sound,
            SoundCategory category,
            float volume,
            float pitch,
            long seed,
            CallbackInfo ci
    ) {
        if (SilencerBatClientSoundGate.shouldSuppress(sound, category)) {
            ci.cancel();
        }
    }
}
