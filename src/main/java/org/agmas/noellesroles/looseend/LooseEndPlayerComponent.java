package org.agmas.noellesroles.looseend;

import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class LooseEndPlayerComponent implements AutoSyncedComponent {
    public static final int OPENING_PHASE_TICKS = 20 * 30;

    public static final ComponentKey<LooseEndPlayerComponent> KEY = ComponentRegistry.getOrCreate(
        Identifier.of(Noellesroles.MOD_ID, "loose_end"),
        LooseEndPlayerComponent.class
    );

    private final PlayerEntity player;
    private int riotShieldBlockedDeaths = 0;
    private int pendingRevolverCooldownTicks = 0;
    private int openingPhaseTicks = 0;

    public LooseEndPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.riotShieldBlockedDeaths = 0;
        this.pendingRevolverCooldownTicks = 0;
        this.openingPhaseTicks = 0;
        clearOpeningPhaseState();
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity recipient) {
        return true;
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.riotShieldBlockedDeaths);
        buf.writeInt(this.openingPhaseTicks);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.riotShieldBlockedDeaths = buf.readInt();
        this.openingPhaseTicks = buf.readInt();
    }

    public int registerBlockedDeath() {
        this.riotShieldBlockedDeaths++;
        this.sync();
        return this.riotShieldBlockedDeaths;
    }

    public int getRiotShieldBlockedDeaths() {
        return this.riotShieldBlockedDeaths;
    }

    public void consumeOneRiotShield() {
        if (this.player.getMainHandStack().isOf(ModItems.RIOT_SHIELD)) {
            this.player.getMainHandStack().decrement(1);
            return;
        }

        if (this.player.getOffHandStack().isOf(ModItems.RIOT_SHIELD)) {
            this.player.getOffHandStack().decrement(1);
            return;
        }

        for (ItemStack stack : this.player.getInventory().main) {
            if (stack.isOf(ModItems.RIOT_SHIELD)) {
                stack.decrement(1);
                return;
            }
        }
    }

    public void consumeOneRevolver() {
        this.removeOneItem(WatheItems.REVOLVER);
    }

    public void queueRevolverCooldown(int ticks) {
        this.pendingRevolverCooldownTicks = ticks;
    }

    public int consumeQueuedRevolverCooldown() {
        int ticks = this.pendingRevolverCooldownTicks;
        this.pendingRevolverCooldownTicks = 0;
        return ticks;
    }

    public void startOpeningPhase() {
        this.openingPhaseTicks = OPENING_PHASE_TICKS;
        applyOpeningPhaseState();
        this.sync();
    }

    public boolean isOpeningPhased() {
        return this.openingPhaseTicks > 0;
    }

    public int getOpeningPhaseTicks() {
        return this.openingPhaseTicks;
    }

    public void tickOpeningPhaseState() {
        if (this.openingPhaseTicks <= 0) {
            return;
        }

        this.openingPhaseTicks--;
        if (this.openingPhaseTicks > 0) {
            applyOpeningPhaseState();
        } else {
            clearOpeningPhaseState();
        }
        this.sync();
    }

    private void applyOpeningPhaseState() {
        this.player.noClip = false;
        this.player.setInvisible(true);
        this.player.setOnGround(true);
        this.player.fallDistance = 0.0F;
    }

    private void clearOpeningPhaseState() {
        this.player.noClip = false;
        this.player.setInvisible(false);
        this.player.setOnGround(true);
        this.player.fallDistance = 0.0F;
        this.player.setVelocity(Vec3d.ZERO);
    }

    private void removeOneItem(net.minecraft.item.Item item) {
        if (this.player.getMainHandStack().isOf(item)) {
            this.player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, ItemStack.EMPTY);
            return;
        }

        if (this.player.getOffHandStack().isOf(item)) {
            this.player.setStackInHand(net.minecraft.util.Hand.OFF_HAND, ItemStack.EMPTY);
            return;
        }

        for (int i = 0; i < this.player.getInventory().size(); i++) {
            if (this.player.getInventory().getStack(i).isOf(item)) {
                this.player.getInventory().setStack(i, ItemStack.EMPTY);
                return;
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("riotShieldBlockedDeaths", this.riotShieldBlockedDeaths);
        tag.putInt("pendingRevolverCooldownTicks", this.pendingRevolverCooldownTicks);
        tag.putInt("openingPhaseTicks", this.openingPhaseTicks);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.riotShieldBlockedDeaths = tag.contains("riotShieldBlockedDeaths") ? tag.getInt("riotShieldBlockedDeaths") : 0;
        this.pendingRevolverCooldownTicks = tag.contains("pendingRevolverCooldownTicks") ? tag.getInt("pendingRevolverCooldownTicks") : 0;
        this.openingPhaseTicks = tag.contains("openingPhaseTicks") ? tag.getInt("openingPhaseTicks") : 0;
    }
}
