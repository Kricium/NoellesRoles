package org.agmas.noellesroles.criminalreasoner;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriminalReasonerPlayerComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<CriminalReasonerPlayerComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "criminal_reasoner"), CriminalReasonerPlayerComponent.class);

    private final PlayerEntity player;
    private final Map<UUID, UUID> victimToKiller = new HashMap<>();
    private final Map<UUID, Boolean> solvedVictims = new HashMap<>();
    private int successfulReasoningCount;

    public CriminalReasonerPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void reset() {
        this.victimToKiller.clear();
        this.solvedVictims.clear();
        this.successfulReasoningCount = 0;
        this.sync();
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void recordReasoningTarget(UUID victimUuid, UUID killerUuid) {
        if (victimUuid == null || killerUuid == null) {
            return;
        }

        // 保存每名死者最后一次有效击杀者，供犯罪推理学家后续进行匹配。
        this.victimToKiller.put(victimUuid, killerUuid);
        this.sync();
    }

    public boolean isCorrectReasoning(UUID victimUuid, UUID suspectUuid) {
        if (victimUuid == null || suspectUuid == null) {
            return false;
        }

        UUID actualKiller = this.victimToKiller.get(victimUuid);
        return suspectUuid.equals(actualKiller);
    }

    public boolean recordSuccessfulReasoning(UUID victimUuid) {
        if (victimUuid == null || this.solvedVictims.containsKey(victimUuid)) {
            return false;
        }

        // 同一名死者只记一次成功推理，避免重复推理刷胜利进度。
        this.solvedVictims.put(victimUuid, true);
        this.successfulReasoningCount++;
        this.sync();
        return true;
    }

    public int getSuccessfulReasoningCount() {
        return this.successfulReasoningCount;
    }

    public boolean hasSolvedVictim(UUID victimUuid) {
        return victimUuid != null && this.solvedVictims.containsKey(victimUuid);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    @Override
    public void serverTick() {
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.victimToKiller.clear();
        this.solvedVictims.clear();
        this.successfulReasoningCount = nbtCompound.getInt("successfulReasoningCount");
        if (nbtCompound.contains("victimToKiller")) {
            NbtCompound mappingTag = nbtCompound.getCompound("victimToKiller");
            for (String key : mappingTag.getKeys()) {
                try {
                    UUID victimUuid = UUID.fromString(key);
                    UUID killerUuid = mappingTag.getUuid(key);
                    this.victimToKiller.put(victimUuid, killerUuid);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        if (nbtCompound.contains("solvedVictims")) {
            NbtCompound solvedTag = nbtCompound.getCompound("solvedVictims");
            for (String key : solvedTag.getKeys()) {
                try {
                    this.solvedVictims.put(UUID.fromString(key), true);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound mappingTag = new NbtCompound();
        for (Map.Entry<UUID, UUID> entry : this.victimToKiller.entrySet()) {
            mappingTag.putUuid(entry.getKey().toString(), entry.getValue());
        }
        nbtCompound.put("victimToKiller", mappingTag);

        NbtCompound solvedTag = new NbtCompound();
        for (UUID solvedVictim : this.solvedVictims.keySet()) {
            solvedTag.putBoolean(solvedVictim.toString(), true);
        }
        nbtCompound.put("solvedVictims", solvedTag);
        nbtCompound.putInt("successfulReasoningCount", this.successfulReasoningCount);
    }
}
