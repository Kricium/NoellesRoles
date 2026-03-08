package org.agmas.noellesroles.noisemaker;

import dev.doctor4t.wathe.game.GameConstants;
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
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

/**
 * 大嗓门玩家组件
 * 追踪广播状态（是否正在广播语音给所有玩家）
 */
public class NoisemakerPlayerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<NoisemakerPlayerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "noisemaker"),
            NoisemakerPlayerComponent.class
    );

    public static final int BROADCAST_DURATION_TICKS = GameConstants.getInTicks(0, 10); // 10 seconds

    private final PlayerEntity player;
    private int broadcastTicksRemaining = 0;

    public NoisemakerPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.broadcastTicksRemaining = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    public void startBroadcasting() {
        this.broadcastTicksRemaining = BROADCAST_DURATION_TICKS;
        this.sync();
    }

    public boolean isBroadcasting() {
        return this.broadcastTicksRemaining > 0;
    }

    public int getBroadcastTicksRemaining() {
        return this.broadcastTicksRemaining;
    }

    @Override
    public void serverTick() {
        if (this.broadcastTicksRemaining > 0) {
            this.broadcastTicksRemaining--;
            if (this.broadcastTicksRemaining % 20 == 0) {
                this.sync();
            }
        }
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.broadcastTicksRemaining);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.broadcastTicksRemaining = buf.readInt();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("broadcastTicksRemaining", this.broadcastTicksRemaining);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.broadcastTicksRemaining = tag.contains("broadcastTicksRemaining") ? tag.getInt("broadcastTicksRemaining") : 0;
    }
}
