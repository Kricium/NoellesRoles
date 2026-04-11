package org.agmas.noellesroles.saint;

import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class SaintPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<SaintPlayerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "saint"), SaintPlayerComponent.class
    );

    public static final int NORMAL_KARMA_LOCK_TICKS = GameConstants.getInTicks(0, 5);
    public static final int BOMBER_KARMA_LOCK_TICKS = GameConstants.getInTicks(0, 20);
    public static final int HELLFIRE_ACTIVE_TICKS = GameConstants.getInTicks(0, 15);
    public static final int HELLFIRE_COOLDOWN_TICKS = GameConstants.getInTicks(1, 0);

    private final PlayerEntity player;
    private boolean karmaMarked;
    private int karmaLockTicks;
    private int hellfireActiveTicks;

    public SaintPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.karmaMarked = false;
        this.karmaLockTicks = 0;
        this.hellfireActiveTicks = 0;
        this.sync();
    }

    public void markKarma() {
        this.karmaMarked = true;
        this.sync();
    }

    public boolean hasKarma() {
        return this.karmaMarked;
    }

    public boolean isKarmaLocked() {
        return this.karmaLockTicks > 0;
    }

    public int getKarmaLockTicks() {
        return this.karmaLockTicks;
    }

    public boolean isHellfireActive() {
        return this.hellfireActiveTicks > 0;
    }

    public int getHellfireActiveTicks() {
        return this.hellfireActiveTicks;
    }

    public void activateHellfire() {
        this.hellfireActiveTicks = HELLFIRE_ACTIVE_TICKS;
        this.sync();
    }

    public void clearHellfire() {
        if (this.hellfireActiveTicks <= 0) {
            return;
        }
        this.hellfireActiveTicks = 0;
        this.sync();
    }

    public void triggerKarmaLock(boolean bomber) {
        this.karmaLockTicks = bomber ? BOMBER_KARMA_LOCK_TICKS : NORMAL_KARMA_LOCK_TICKS;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity recipient) {
        return recipient == this.player;
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(this.karmaMarked);
        buf.writeInt(this.karmaLockTicks);
        buf.writeInt(this.hellfireActiveTicks);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.karmaMarked = buf.readBoolean();
        this.karmaLockTicks = buf.readInt();
        this.hellfireActiveTicks = buf.readInt();
    }

    @Override
    public void serverTick() {
        if (this.karmaLockTicks > 0) {
            this.karmaLockTicks--;
            if (this.karmaLockTicks == 0 || this.karmaLockTicks % 10 == 0) {
                this.sync();
            }
        }

        if (this.hellfireActiveTicks > 0) {
            this.hellfireActiveTicks--;
            if (this.hellfireActiveTicks == 0) {
                if (this.player instanceof ServerPlayerEntity serverPlayer) {
                    if (GameFunctions.isPlayerPlayingAndAlive(serverPlayer)) {
                        org.agmas.noellesroles.AbilityPlayerComponent.KEY.get(serverPlayer).setCooldown(HELLFIRE_COOLDOWN_TICKS);
                    } else {
                        this.sync();
                    }
                } else {
                    this.sync();
                }
            } else if (this.hellfireActiveTicks % 10 == 0) {
                this.sync();
            }
        }
    }

    @Override
    public void clientTick() {
        if (this.karmaLockTicks > 0) {
            this.karmaLockTicks--;
        }
        if (this.hellfireActiveTicks > 0) {
            this.hellfireActiveTicks--;
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("karmaMarked", this.karmaMarked);
        tag.putInt("karmaLockTicks", this.karmaLockTicks);
        tag.putInt("hellfireActiveTicks", this.hellfireActiveTicks);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.karmaMarked = tag.getBoolean("karmaMarked");
        this.karmaLockTicks = tag.getInt("karmaLockTicks");
        this.hellfireActiveTicks = tag.contains("hellfireActiveTicks") ? tag.getInt("hellfireActiveTicks") : 0;
    }
}
