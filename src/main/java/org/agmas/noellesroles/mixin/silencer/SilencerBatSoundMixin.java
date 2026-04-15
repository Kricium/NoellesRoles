package org.agmas.noellesroles.mixin.silencer;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class SilencerBatSoundMixin {
    private static final Identifier NOELLESROLES$BAT_HIT_SOUND_ID = WatheSounds.ITEM_BAT_HIT.getId();

    /**
     * 静语者使用疯魔球棒命中时不播放敲击音效。
     */
    @Inject(
            method = "playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noellesroles$silenceSilencerBatHitSound(
            PlayerEntity except,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundCategory category,
            float volume,
            float pitch,
            CallbackInfo ci
    ) {
        if (!noellesroles$shouldSilenceBatHit(except, sound)) {
            return;
        }

        ci.cancel();
    }

    /**
     * 覆盖带 seed 的同一路径，避免无 seed 重载继续转发后仍然播音。
     */
    @Inject(
            method = "playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFJ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noellesroles$silenceSilencerBatHitSoundSeeded(
            PlayerEntity except,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundCategory category,
            float volume,
            float pitch,
            long seed,
            CallbackInfo ci
    ) {
        if (!noellesroles$shouldSilenceBatHit(except, sound)) {
            return;
        }

        ci.cancel();
    }

    private static boolean noellesroles$shouldSilenceBatHit(PlayerEntity except, SoundEvent sound) {
        if (except == null || sound == null) {
            return false;
        }
        if (!NOELLESROLES$BAT_HIT_SOUND_ID.equals(Registries.SOUND_EVENT.getId(sound))) {
            return false;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(except.getWorld());
        return gameWorld.isRole(except, Noellesroles.SILENCER);
    }
}
