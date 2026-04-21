package org.agmas.noellesroles.deatharena;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class DeathArenaPlayerComponent implements AutoSyncedComponent {
    public static final ComponentKey<DeathArenaPlayerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "death_arena_player"),
            DeathArenaPlayerComponent.class
    );

    private final PlayerEntity player;
    private boolean inArena = false;
    private boolean pendingRespawn = false;
    private boolean autoExit = false;
    private Vec3d returnPos = Vec3d.ZERO;
    private float returnYaw = 0.0F;
    private float returnPitch = 0.0F;
    private GameMode previousGameMode = GameMode.SPECTATOR;
    private Identifier returnWorldId = World.OVERWORLD.getValue();
    private long respawnAtTick = 0L;

    public DeathArenaPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.inArena = false;
        this.pendingRespawn = false;
        this.autoExit = false;
        this.returnPos = Vec3d.ZERO;
        this.returnYaw = 0.0F;
        this.returnPitch = 0.0F;
        this.previousGameMode = GameMode.SPECTATOR;
        this.returnWorldId = World.OVERWORLD.getValue();
        this.respawnAtTick = 0L;
        sync();
    }

    public void enter(Identifier returnWorldId, Vec3d returnPos, float yaw, float pitch, GameMode previousGameMode) {
        this.inArena = true;
        this.pendingRespawn = false;
        this.autoExit = false;
        this.returnWorldId = returnWorldId;
        this.returnPos = returnPos;
        this.returnYaw = yaw;
        this.returnPitch = pitch;
        this.previousGameMode = previousGameMode;
        this.respawnAtTick = 0L;
        sync();
    }

    public void leave(boolean autoExit) {
        this.inArena = false;
        this.pendingRespawn = false;
        this.autoExit = autoExit;
        this.respawnAtTick = 0L;
        sync();
    }

    public boolean isInArena() {
        return inArena;
    }

    public boolean isPendingRespawn() {
        return pendingRespawn;
    }

    public void setPendingRespawn(boolean pendingRespawn) {
        this.pendingRespawn = pendingRespawn;
        sync();
    }

    public boolean isAutoExit() {
        return autoExit;
    }

    public Vec3d getReturnPos() {
        return returnPos;
    }

    public float getReturnYaw() {
        return returnYaw;
    }

    public float getReturnPitch() {
        return returnPitch;
    }

    public GameMode getPreviousGameMode() {
        return previousGameMode;
    }

    public Identifier getReturnWorldId() {
        return returnWorldId;
    }

    public long getRespawnAtTick() {
        return respawnAtTick;
    }

    public void scheduleRespawnAt(long respawnAtTick) {
        this.pendingRespawn = true;
        this.respawnAtTick = respawnAtTick;
        sync();
    }

    public void sync() {
        KEY.sync(player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity recipient) {
        return recipient == player;
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeBoolean(inArena);
        buf.writeBoolean(pendingRespawn);
        buf.writeBoolean(autoExit);
        buf.writeLong(respawnAtTick);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.inArena = buf.readBoolean();
        this.pendingRespawn = buf.readBoolean();
        this.autoExit = buf.readBoolean();
        this.respawnAtTick = buf.readLong();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putBoolean("inArena", inArena);
        tag.putBoolean("pendingRespawn", pendingRespawn);
        tag.putBoolean("autoExit", autoExit);
        tag.putString("returnWorldId", returnWorldId.toString());
        tag.putDouble("returnX", returnPos.x);
        tag.putDouble("returnY", returnPos.y);
        tag.putDouble("returnZ", returnPos.z);
        tag.putFloat("returnYaw", returnYaw);
        tag.putFloat("returnPitch", returnPitch);
        tag.putString("previousGameMode", previousGameMode.getName());
        tag.putLong("respawnAtTick", respawnAtTick);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.inArena = tag.getBoolean("inArena");
        this.pendingRespawn = tag.getBoolean("pendingRespawn");
        this.autoExit = tag.getBoolean("autoExit");
        this.returnWorldId = Identifier.tryParse(tag.getString("returnWorldId"));
        if (this.returnWorldId == null) {
            this.returnWorldId = World.OVERWORLD.getValue();
        }
        this.returnPos = new Vec3d(
                tag.getDouble("returnX"),
                tag.getDouble("returnY"),
                tag.getDouble("returnZ")
        );
        this.returnYaw = tag.getFloat("returnYaw");
        this.returnPitch = tag.getFloat("returnPitch");
        this.previousGameMode = GameMode.byName(tag.getString("previousGameMode"), GameMode.SPECTATOR);
        this.respawnAtTick = tag.getLong("respawnAtTick");
    }
}
