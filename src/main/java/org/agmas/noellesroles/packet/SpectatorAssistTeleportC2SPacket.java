package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.UUID;

public record SpectatorAssistTeleportC2SPacket(UUID targetUuid) implements CustomPayload {
    public static final Id<SpectatorAssistTeleportC2SPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "spectator_assist_teleport"));

    public static final PacketCodec<RegistryByteBuf, SpectatorAssistTeleportC2SPacket> CODEC =
            PacketCodec.of(SpectatorAssistTeleportC2SPacket::write, SpectatorAssistTeleportC2SPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(targetUuid);
    }

    private static SpectatorAssistTeleportC2SPacket read(PacketByteBuf buf) {
        return new SpectatorAssistTeleportC2SPacket(buf.readUuid());
    }
}
