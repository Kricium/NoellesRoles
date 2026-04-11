package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SpectatorInfoSyncS2CPacket(long requestId, long matchStartTick, List<Entry> entries) implements CustomPayload {
    public static final Id<SpectatorInfoSyncS2CPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "spectator_info_sync"));

    public static final PacketCodec<RegistryByteBuf, SpectatorInfoSyncS2CPacket> CODEC =
            PacketCodec.of(SpectatorInfoSyncS2CPacket::write, SpectatorInfoSyncS2CPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarLong(requestId);
        buf.writeVarLong(matchStartTick);
        buf.writeVarInt(entries.size());
        for (Entry entry : entries) {
            buf.writeUuid(entry.uuid());
            buf.writeString(entry.roleTranslationKey(), 128);
            buf.writeInt(entry.roleColor());
            buf.writeString(entry.deathReasonRaw(), 256);
            buf.writeVarInt(entry.deathAgeSeconds());
            buf.writeVarInt(entry.replayLines().size());
            for (String line : entry.replayLines()) {
                buf.writeString(line, 512);
            }
        }
    }

    private static SpectatorInfoSyncS2CPacket read(PacketByteBuf buf) {
        long requestId = buf.readVarLong();
        long matchStartTick = buf.readVarLong();
        int size = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUuid();
            String roleTranslationKey = buf.readString(128);
            int roleColor = buf.readInt();
            String deathReasonRaw = buf.readString(256);
            int deathAgeSeconds = buf.readVarInt();
            int lineCount = buf.readVarInt();
            List<String> replayLines = new ArrayList<>(lineCount);
            for (int j = 0; j < lineCount; j++) {
                replayLines.add(buf.readString(512));
            }
            entries.add(new Entry(uuid, roleTranslationKey, roleColor, deathReasonRaw, deathAgeSeconds, replayLines));
        }
        return new SpectatorInfoSyncS2CPacket(requestId, matchStartTick, entries);
    }

    public record Entry(UUID uuid, String roleTranslationKey, int roleColor, String deathReasonRaw, int deathAgeSeconds, List<String> replayLines) {
    }
}

