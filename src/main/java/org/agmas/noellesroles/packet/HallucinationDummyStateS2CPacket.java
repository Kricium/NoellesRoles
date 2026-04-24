package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.hallucination.HallucinationDummyKind;
import org.agmas.noellesroles.hallucination.HallucinationDummyState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record HallucinationDummyStateS2CPacket(
        List<HallucinationDummyState> added,
        List<UUID> removed,
        List<UUID> fakeBodies,
        Map<UUID, Identifier> fakeBodyDeathReasons
) implements CustomPayload {
    public static final Id<HallucinationDummyStateS2CPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "hallucination_dummy_state"));

    public static final PacketCodec<RegistryByteBuf, HallucinationDummyStateS2CPacket> CODEC =
            PacketCodec.of(HallucinationDummyStateS2CPacket::write, HallucinationDummyStateS2CPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.added.size());
        for (HallucinationDummyState state : this.added) {
            buf.writeUuid(state.id());
            buf.writeEnumConstant(state.kind());
            buf.writeUuid(state.skinUuid());
            buf.writeString(state.skinName(), 64);
            buf.writeDouble(state.position().x);
            buf.writeDouble(state.position().y);
            buf.writeDouble(state.position().z);
            buf.writeBoolean(state.collidable());
            buf.writeVarInt(state.localEntityId());
            buf.writeFloat(state.bodyYaw());
        }
        buf.writeVarInt(this.removed.size());
        for (UUID uuid : this.removed) {
            buf.writeUuid(uuid);
        }
        buf.writeVarInt(this.fakeBodies.size());
        for (UUID uuid : this.fakeBodies) {
            buf.writeUuid(uuid);
        }
        buf.writeVarInt(this.fakeBodyDeathReasons.size());
        for (Map.Entry<UUID, Identifier> entry : this.fakeBodyDeathReasons.entrySet()) {
            buf.writeUuid(entry.getKey());
            buf.writeIdentifier(entry.getValue());
        }
    }

    private static HallucinationDummyStateS2CPacket read(PacketByteBuf buf) {
        int addedSize = buf.readVarInt();
        List<HallucinationDummyState> added = new ArrayList<>(addedSize);
        for (int i = 0; i < addedSize; i++) {
            UUID id = buf.readUuid();
            HallucinationDummyKind kind = buf.readEnumConstant(HallucinationDummyKind.class);
            UUID skinUuid = buf.readUuid();
            String skinName = buf.readString(64);
            Vec3d position = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            boolean collidable = buf.readBoolean();
            int localEntityId = buf.readVarInt();
            float bodyYaw = buf.readFloat();
            added.add(new HallucinationDummyState(id, kind, skinUuid, skinName, position, collidable, localEntityId, bodyYaw));
        }

        int removedSize = buf.readVarInt();
        List<UUID> removed = new ArrayList<>(removedSize);
        for (int i = 0; i < removedSize; i++) {
            removed.add(buf.readUuid());
        }

        int bodySize = buf.readVarInt();
        List<UUID> fakeBodies = new ArrayList<>(bodySize);
        for (int i = 0; i < bodySize; i++) {
            fakeBodies.add(buf.readUuid());
        }

        int reasonSize = buf.readVarInt();
        Map<UUID, Identifier> fakeBodyDeathReasons = new HashMap<>(reasonSize);
        for (int i = 0; i < reasonSize; i++) {
            fakeBodyDeathReasons.put(buf.readUuid(), buf.readIdentifier());
        }

        return new HallucinationDummyStateS2CPacket(added, removed, fakeBodies, fakeBodyDeathReasons);
    }
}
