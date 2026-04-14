package org.agmas.noellesroles.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

public record FerrymanBodyAgeSyncS2CPacket(int entityId, int age) implements CustomPayload {
    public static final Id<FerrymanBodyAgeSyncS2CPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "ferryman_body_age_sync"));

    public static final PacketCodec<RegistryByteBuf, FerrymanBodyAgeSyncS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, FerrymanBodyAgeSyncS2CPacket::entityId,
            PacketCodecs.INTEGER, FerrymanBodyAgeSyncS2CPacket::age,
            FerrymanBodyAgeSyncS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
