package org.agmas.noellesroles.looseend;

import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class LooseEndsRadarWorldComponent implements AutoSyncedComponent {
    public static final ComponentKey<LooseEndsRadarWorldComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "loose_ends_radar"),
            LooseEndsRadarWorldComponent.class
    );

    public static final int COUNTDOWN_TICKS = GameConstants.getInTicks(1, 0);
    public static final int SCAN_TICKS = GameConstants.getInTicks(0, 5);
    private static final int FIRST_COUNTDOWN_TICKS = GameConstants.getInTicks(3, 0);

    private final World world;
    private RadarPhase phase = RadarPhase.COUNTDOWN;
    private int ticksRemaining = 0;

    public LooseEndsRadarWorldComponent(World world) {
        this.world = world;
    }

    public void startRound() {
        this.phase = RadarPhase.COUNTDOWN;
        this.ticksRemaining = FIRST_COUNTDOWN_TICKS;
    }

    public void startCountdown() {
        this.phase = RadarPhase.COUNTDOWN;
        this.ticksRemaining = COUNTDOWN_TICKS;
    }

    public void startScan() {
        this.phase = RadarPhase.SCANNING;
        this.ticksRemaining = SCAN_TICKS;
    }

    public void reset() {
        this.phase = RadarPhase.INACTIVE;
        this.ticksRemaining = 0;
    }

    public boolean isInactive() {
        return this.phase == RadarPhase.INACTIVE;
    }

    public boolean isCountdown() {
        return this.phase == RadarPhase.COUNTDOWN;
    }

    public boolean isScanning() {
        return this.phase == RadarPhase.SCANNING;
    }

    public int tick() {
        if (this.phase == RadarPhase.INACTIVE || this.ticksRemaining <= 0) {
            return this.ticksRemaining;
        }
        this.ticksRemaining--;
        return this.ticksRemaining;
    }

    public int getTicksRemaining() {
        return this.ticksRemaining;
    }

    public int getSecondsRemaining() {
        return Math.max(1, (this.ticksRemaining + 19) / 20);
    }

    public boolean shouldRenderHud() {
        return this.phase == RadarPhase.COUNTDOWN || this.phase == RadarPhase.SCANNING;
    }

    public int getHudTimeTicks() {
        return Math.max(0, this.ticksRemaining);
    }

    public String getHudLabelKey() {
        return this.phase == RadarPhase.SCANNING
                ? "subtitle.noellesroles.loose_ends_radar_active"
                : "subtitle.noellesroles.loose_ends_radar_countdown";
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.phase.ordinal());
        buf.writeInt(this.ticksRemaining);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        int ordinal = buf.readInt();
        RadarPhase[] values = RadarPhase.values();
        this.phase = ordinal >= 0 && ordinal < values.length ? values[ordinal] : RadarPhase.INACTIVE;
        this.ticksRemaining = buf.readInt();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("phase", this.phase.name());
        tag.putInt("ticksRemaining", this.ticksRemaining);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        try {
            this.phase = RadarPhase.valueOf(tag.getString("phase"));
        } catch (IllegalArgumentException ignored) {
            this.phase = RadarPhase.INACTIVE;
        }
        this.ticksRemaining = tag.getInt("ticksRemaining");
    }

    public enum RadarPhase {
        INACTIVE,
        COUNTDOWN,
        SCANNING
    }
}
