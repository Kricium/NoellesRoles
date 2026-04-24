package org.agmas.noellesroles.vulture;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VulturePlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<VulturePlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "vulture"), VulturePlayerComponent.class);
    private final PlayerEntity player;
    private int bodiesEaten = 0;
    private int bodiesRequired = 0;
    private boolean won = false;
    private final List<UUID> eatenBodies = new ArrayList<>();
    private int highlightTicks = 0;
    private double highlightRange = 0.0D;

    public VulturePlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.bodiesEaten = 0;
        this.bodiesRequired = 0;
        this.won = false;
        this.eatenBodies.clear();
        this.highlightTicks = 0;
        this.highlightRange = 0.0D;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    // Getters
    public int getBodiesEaten() {
        return bodiesEaten;
    }

    public int getBodiesRequired() {
        return bodiesRequired;
    }

    public boolean hasWon() {
        return won;
    }

    public List<UUID> getEatenBodies() {
        return new ArrayList<>(eatenBodies);
    }

    // Setters
    public void setBodiesRequired(int bodiesRequired) {
        this.bodiesRequired = bodiesRequired;
        this.sync();
    }

    /**
     * 添加一具被吃掉的尸体，同时检查胜利条件
     * @param bodyUuid 尸体的UUID
     * @return 是否达成胜利条件
     */
    public boolean addBody(UUID bodyUuid) {
        this.bodiesEaten++;
        this.eatenBodies.add(bodyUuid);

        if (this.bodiesEaten >= this.bodiesRequired) {
            this.won = true;
        }

        this.sync();
        return this.won;
    }

    public int getHighlightTicks() {
        return highlightTicks;
    }

    public void setHighlightTicks(int ticks) {
        this.highlightTicks = ticks;
        if (ticks <= 0) {
            this.highlightRange = 0.0D;
        }
        this.sync();
    }

    public double getHighlightRange() {
        return highlightRange;
    }

    public boolean hasActiveHighlight() {
        return this.highlightTicks > 0;
    }

    public boolean isEntityWithinHighlightRange(PlayerEntity target) {
        if (!hasActiveHighlight() || target == null || target == this.player) {
            return false;
        }
        if (this.highlightRange <= 0.0D) {
            return true;
        }
        return this.player.squaredDistanceTo(target) <= this.highlightRange * this.highlightRange;
    }

    public void activateHighlight(int ticks, double range) {
        this.highlightTicks = Math.max(ticks, 0);
        this.highlightRange = this.highlightTicks > 0 ? Math.max(range, 0.0D) : 0.0D;
        this.sync();
    }

    @Override
    public void clientTick() {
        if (this.highlightTicks > 0) {
            this.highlightTicks--;
            if (this.highlightTicks == 0) {
                this.highlightRange = 0.0D;
            }
        }
    }

    @Override
    public void serverTick() {
        if (this.highlightTicks > 0) {
            this.highlightTicks--;
            if (this.highlightTicks == 0) {
                this.highlightRange = 0.0D;
                this.sync();
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("bodiesEaten", this.bodiesEaten);
        tag.putInt("bodiesRequired", this.bodiesRequired);
        tag.putBoolean("won", this.won);
        tag.putInt("highlightTicks", this.highlightTicks);
        tag.putDouble("highlightRange", this.highlightRange);

        NbtList eatenList = new NbtList();
        for (UUID uuid : this.eatenBodies) {
            eatenList.add(NbtString.of(uuid.toString()));
        }
        tag.put("eatenBodies", eatenList);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.bodiesEaten = tag.contains("bodiesEaten") ? tag.getInt("bodiesEaten") : 0;
        this.bodiesRequired = tag.contains("bodiesRequired") ? tag.getInt("bodiesRequired") : 0;
        this.won = tag.getBoolean("won");
        this.highlightTicks = tag.contains("highlightTicks") ? tag.getInt("highlightTicks") : 0;
        this.highlightRange = this.highlightTicks > 0 && tag.contains("highlightRange") ? tag.getDouble("highlightRange") : 0.0D;

        this.eatenBodies.clear();
        if (tag.contains("eatenBodies")) {
            NbtList eatenList = tag.getList("eatenBodies", NbtString.STRING_TYPE);
            for (int i = 0; i < eatenList.size(); i++) {
                this.eatenBodies.add(UUID.fromString(eatenList.getString(i)));
            }
        }
    }
}
