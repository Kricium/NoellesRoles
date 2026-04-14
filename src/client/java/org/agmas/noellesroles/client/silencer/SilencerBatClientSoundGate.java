package org.agmas.noellesroles.client.silencer;

import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Set;

/**
 * 在客户端短时间屏蔽静语者球棒命中时的本地残留攻击音，
 * 只影响本次球棒击杀附近的一小段时间窗口。
 */
public final class SilencerBatClientSoundGate {
    private static final long SUPPRESS_DURATION_NANOS = 750_000_000L;
    private static final Set<Identifier> SUPPRESSED_SOUND_IDS = Set.of(
            WatheSounds.ITEM_BAT_HIT.getId(),
            SoundEvents.ENTITY_PLAYER_ATTACK_STRONG.getId(),
            SoundEvents.ENTITY_PLAYER_ATTACK_WEAK.getId(),
            SoundEvents.ENTITY_PLAYER_ATTACK_CRIT.getId(),
            SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK.getId(),
            SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP.getId(),
            SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE.getId(),
            SoundEvents.ENTITY_PLAYER_HURT.getId(),
            SoundEvents.ENTITY_PLAYER_DEATH.getId()
    );

    private static long suppressUntilNanos = Long.MIN_VALUE;

    private SilencerBatClientSoundGate() {
    }

    public static void arm() {
        suppressUntilNanos = System.nanoTime() + SUPPRESS_DURATION_NANOS;
    }

    public static boolean shouldSuppress(SoundEvent sound, SoundCategory category) {
        if (sound == null || category != SoundCategory.PLAYERS) {
            return false;
        }
        if (System.nanoTime() > suppressUntilNanos) {
            return false;
        }

        return SUPPRESSED_SOUND_IDS.contains(sound.getId());
    }

    public static boolean shouldSuppress(RegistryEntry<SoundEvent> sound, SoundCategory category) {
        if (sound == null) {
            return false;
        }
        return shouldSuppress(sound.value(), category);
    }
}
