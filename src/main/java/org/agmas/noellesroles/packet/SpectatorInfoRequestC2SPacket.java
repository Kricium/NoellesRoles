package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

public record SpectatorInfoRequestC2SPacket(long requestId) implements CustomPayload {
    public static final Id<SpectatorInfoRequestC2SPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "spectator_info_request"));

    public static final PacketCodec<RegistryByteBuf, SpectatorInfoRequestC2SPacket> CODEC =
            PacketCodec.of(SpectatorInfoRequestC2SPacket::write, SpectatorInfoRequestC2SPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarLong(requestId);
    }

    private static SpectatorInfoRequestC2SPacket read(PacketByteBuf buf) {
        return new SpectatorInfoRequestC2SPacket(buf.readVarLong());
    }
}

