package org.agmas.noellesroles.commander;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommanderPlayerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<CommanderPlayerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "commander"),
            CommanderPlayerComponent.class
    );

    public static final int MAX_ACTIVE_MARKS = 2;
    public static final int LAST_BULLET_DELAY_TICKS = 20 * 5;

    private final PlayerEntity player;
    private final List<UUID> threatTargets = new ArrayList<>();
    private final List<String> threatTargetNames = new ArrayList<>();
    private int usedMarks = 0;
    private boolean introBroadcasted = false;
    private int lastBulletCountdownTicks = 0;

    public CommanderPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.threatTargets.clear();
        this.threatTargetNames.clear();
        this.usedMarks = 0;
        this.introBroadcasted = false;
        this.lastBulletCountdownTicks = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        if (player == this.player) {
            return true;
        }
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(this.player.getWorld());
        return GameFunctions.isPlayerPlayingAndAlive(player)
                && CommanderHelper.isNotificationRecipient(gameWorldComponent, player);
    }

    public boolean canMarkMore() {
        return this.usedMarks < getMaxMarks();
    }

    public int getUsedMarks() {
        return this.usedMarks;
    }

    public int getMaxMarks() {
        return Math.floorDiv(GameWorldComponent.KEY.get(this.player.getWorld()).getAllPlayers().size(), 3);
    }

    public int getRemainingMarks() {
        return Math.max(0, getMaxMarks() - this.usedMarks);
    }

    public boolean addThreatTarget(UUID targetUuid, String targetName) {
        if (!canMarkMore() || isThreatTarget(targetUuid)) {
            return false;
        }
        if (this.threatTargets.size() >= MAX_ACTIVE_MARKS) {
            this.threatTargets.remove(0);
            this.threatTargetNames.remove(0);
        }
        this.threatTargets.add(targetUuid);
        this.threatTargetNames.add(targetName);
        this.usedMarks++;
        this.sync();
        return true;
    }

    public boolean isThreatTarget(UUID targetUuid) {
        return this.threatTargets.contains(targetUuid);
    }

    public void removeThreatTarget(UUID targetUuid) {
        int index = this.threatTargets.indexOf(targetUuid);
        if (index < 0) {
            return;
        }
        this.threatTargets.remove(index);
        this.threatTargetNames.remove(index);
        this.sync();
    }

    public List<UUID> getThreatTargets() {
        return List.copyOf(this.threatTargets);
    }

    public List<String> getThreatTargetNames() {
        return List.copyOf(this.threatTargetNames);
    }

    public boolean isIntroBroadcasted() {
        return this.introBroadcasted;
    }

    public void setIntroBroadcasted(boolean introBroadcasted) {
        this.introBroadcasted = introBroadcasted;
    }

    public boolean isLastBulletCountdownActive() {
        return this.lastBulletCountdownTicks > 0;
    }

    public int getLastBulletCountdownTicks() {
        return this.lastBulletCountdownTicks;
    }

    public void startLastBulletCountdown() {
        if (this.lastBulletCountdownTicks > 0) {
            return;
        }
        this.lastBulletCountdownTicks = LAST_BULLET_DELAY_TICKS;
        this.sync();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeInt(this.usedMarks);
        buf.writeInt(this.lastBulletCountdownTicks);
        buf.writeInt(this.threatTargets.size());
        for (int i = 0; i < this.threatTargets.size(); i++) {
            buf.writeUuid(this.threatTargets.get(i));
            buf.writeString(this.threatTargetNames.get(i));
        }
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.usedMarks = buf.readInt();
        this.lastBulletCountdownTicks = buf.readInt();
        this.threatTargets.clear();
        this.threatTargetNames.clear();
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            this.threatTargets.add(buf.readUuid());
            this.threatTargetNames.add(buf.readString());
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("usedMarks", this.usedMarks);
        tag.putBoolean("introBroadcasted", this.introBroadcasted);
        tag.putInt("lastBulletCountdownTicks", this.lastBulletCountdownTicks);

        NbtList targets = new NbtList();
        for (UUID uuid : this.threatTargets) {
            targets.add(NbtString.of(uuid.toString()));
        }
        tag.put("threatTargets", targets);

        NbtList names = new NbtList();
        for (String name : this.threatTargetNames) {
            names.add(NbtString.of(name));
        }
        tag.put("threatTargetNames", names);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.usedMarks = tag.getInt("usedMarks");
        this.introBroadcasted = tag.getBoolean("introBroadcasted");
        this.lastBulletCountdownTicks = tag.getInt("lastBulletCountdownTicks");
        this.threatTargets.clear();
        this.threatTargetNames.clear();

        NbtList targets = tag.getList("threatTargets", NbtString.STRING_TYPE);
        for (int i = 0; i < targets.size(); i++) {
            this.threatTargets.add(UUID.fromString(targets.getString(i)));
        }

        NbtList names = tag.getList("threatTargetNames", NbtString.STRING_TYPE);
        for (int i = 0; i < names.size(); i++) {
            this.threatTargetNames.add(names.getString(i));
        }
    }

    @Override
    public void serverTick() {
        if (this.lastBulletCountdownTicks <= 0 || !(this.player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        this.lastBulletCountdownTicks--;
        if (this.lastBulletCountdownTicks % 20 == 0 || this.lastBulletCountdownTicks <= 0) {
            this.sync();
        }

        if (this.lastBulletCountdownTicks <= 0) {
            dev.doctor4t.wathe.game.GameFunctions.killPlayer(serverPlayer, true, null, Noellesroles.DEATH_REASON_COMMANDER_SUICIDE);
        }
    }
}
