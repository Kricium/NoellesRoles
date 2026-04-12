package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.UUID;

public record SpectatorReplayDetailRequestC2SPacket(long requestId, UUID targetUuid) implements CustomPayload {
    public static final Id<SpectatorReplayDetailRequestC2SPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "spectator_replay_detail_request"));

    public static final PacketCodec<RegistryByteBuf, SpectatorReplayDetailRequestC2SPacket> CODEC =
            PacketCodec.of(SpectatorReplayDetailRequestC2SPacket::write, SpectatorReplayDetailRequestC2SPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarLong(requestId);
        buf.writeUuid(targetUuid);
    }

    private static SpectatorReplayDetailRequestC2SPacket read(PacketByteBuf buf) {
        return new SpectatorReplayDetailRequestC2SPacket(buf.readVarLong(), buf.readUuid());
    }
}

