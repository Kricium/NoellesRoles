package org.agmas.noellesroles.client.mixin.murdermayhem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.agmas.noellesroles.client.hallucination.HallucinationClientVisibilityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class HallucinationClientWorldSoundMixin {
    @Inject(
            method = "playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noellesroles$muteHallucinatedPlayerSounds(
            PlayerEntity except,
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
        if (category != SoundCategory.PLAYERS) {
            return;
        }
        var localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer != null && except != null && HallucinationClientVisibilityHelper.shouldHidePlayer(localPlayer, except)) {
            ci.cancel();
        }
    }
}
