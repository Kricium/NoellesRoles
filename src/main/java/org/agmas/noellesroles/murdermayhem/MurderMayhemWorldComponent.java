package org.agmas.noellesroles.murdermayhem;

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

public class MurderMayhemWorldComponent implements AutoSyncedComponent {
    public static final ComponentKey<MurderMayhemWorldComponent> KEY = ComponentRegistry.getOrCreate(
            Identifier.of(Noellesroles.MOD_ID, "murder_mayhem"),
            MurderMayhemWorldComponent.class
    );

    private static final String EMPTY_EVENT_ID = "";

    private final World world;
    private Identifier currentEventId;
    private boolean eventRolled;
    private boolean introShown;
    private int introTicksRemaining;
    private int fogRadius;
    private int fogAdjustmentTicksRemaining;

    public MurderMayhemWorldComponent(World world) {
        this.world = world;
    }

    public void reset() {
        this.currentEventId = null;
        this.eventRolled = false;
        this.introShown = false;
        this.introTicksRemaining = 0;
        this.fogRadius = 0;
        this.fogAdjustmentTicksRemaining = 0;
    }

    public Identifier getCurrentEventId() {
        return this.currentEventId;
    }

    public MurderMayhemEvent getCurrentEvent() {
        return MurderMayhemEventRegistry.get(this.currentEventId);
    }

    public void setCurrentEventId(Identifier currentEventId) {
        this.currentEventId = currentEventId;
        this.eventRolled = currentEventId != null;
    }

    public boolean isEventRolled() {
        return this.eventRolled;
    }

    public boolean isIntroShown() {
        return this.introShown;
    }

    public void setIntroShown(boolean introShown) {
        this.introShown = introShown;
    }

    public int getIntroTicksRemaining() {
        return this.introTicksRemaining;
    }

    public void startIntroWindow(int introTicksRemaining) {
        this.introTicksRemaining = Math.max(0, introTicksRemaining);
        this.introShown = false;
    }

    public int getFogRadius() {
        return this.fogRadius;
    }

    public void setFogRadius(int fogRadius) {
        this.fogRadius = Math.max(0, fogRadius);
    }

    public int getFogAdjustmentTicksRemaining() {
        return this.fogAdjustmentTicksRemaining;
    }

    public void setFogAdjustmentTicksRemaining(int fogAdjustmentTicksRemaining) {
        this.fogAdjustmentTicksRemaining = Math.max(0, fogAdjustmentTicksRemaining);
    }

    public boolean tickFogAdjustmentWindow() {
        if (this.fogAdjustmentTicksRemaining <= 0) {
            return true;
        }
        this.fogAdjustmentTicksRemaining--;
        return this.fogAdjustmentTicksRemaining <= 0;
    }

    public boolean tickIntroWindow() {
        if (this.introShown || this.introTicksRemaining <= 0) {
            return false;
        }
        this.introTicksRemaining--;
        if (this.introTicksRemaining == 0) {
            this.introShown = true;
            return true;
        }
        return false;
    }

    public void tickClientIntroWindow() {
        if (this.introShown || this.introTicksRemaining <= 0) {
            return;
        }
        this.introTicksRemaining--;
        if (this.introTicksRemaining <= 0) {
            this.introTicksRemaining = 0;
            this.introShown = true;
        }
    }

    public String getCurrentEventDisplayNameKey() {
        MurderMayhemEvent event = getCurrentEvent();
        return event != null ? event.displayNameKey() : "event.noellesroles.murder_mayhem.unknown";
    }

    public String getCurrentEventDescriptionKey() {
        MurderMayhemEvent event = getCurrentEvent();
        return event != null ? event.descriptionKey() : "event_description.noellesroles.murder_mayhem.unknown";
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeString(this.currentEventId == null ? EMPTY_EVENT_ID : this.currentEventId.toString());
        buf.writeBoolean(this.eventRolled);
        buf.writeBoolean(this.introShown);
        buf.writeInt(this.introTicksRemaining);
        buf.writeInt(this.fogRadius);
        buf.writeInt(this.fogAdjustmentTicksRemaining);
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        String rawEventId = buf.readString();
        this.currentEventId = rawEventId.isEmpty() ? null : Identifier.tryParse(rawEventId);
        this.eventRolled = buf.readBoolean();
        this.introShown = buf.readBoolean();
        this.introTicksRemaining = buf.readInt();
        this.fogRadius = buf.readInt();
        this.fogAdjustmentTicksRemaining = buf.readInt();
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putString("currentEventId", this.currentEventId == null ? EMPTY_EVENT_ID : this.currentEventId.toString());
        tag.putBoolean("eventRolled", this.eventRolled);
        tag.putBoolean("introShown", this.introShown);
        tag.putInt("introTicksRemaining", this.introTicksRemaining);
        tag.putInt("fogRadius", this.fogRadius);
        tag.putInt("fogAdjustmentTicksRemaining", this.fogAdjustmentTicksRemaining);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        String rawEventId = tag.getString("currentEventId");
        this.currentEventId = rawEventId.isEmpty() ? null : Identifier.tryParse(rawEventId);
        this.eventRolled = tag.getBoolean("eventRolled");
        this.introShown = tag.getBoolean("introShown");
        this.introTicksRemaining = tag.getInt("introTicksRemaining");
        this.fogRadius = tag.getInt("fogRadius");
        this.fogAdjustmentTicksRemaining = tag.getInt("fogAdjustmentTicksRemaining");
    }
}
