package org.agmas.noellesroles.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.UUID;

public record HallucinationDummyHitC2SPacket(UUID dummyId, Identifier deathReason) implements CustomPayload {
    public static final Id<HallucinationDummyHitC2SPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "hallucination_dummy_hit"));

    public static final PacketCodec<RegistryByteBuf, HallucinationDummyHitC2SPacket> CODEC =
            PacketCodec.of(HallucinationDummyHitC2SPacket::write, HallucinationDummyHitC2SPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.dummyId);
        buf.writeIdentifier(this.deathReason);
    }

    private static HallucinationDummyHitC2SPacket read(PacketByteBuf buf) {
        return new HallucinationDummyHitC2SPacket(buf.readUuid(), buf.readIdentifier());
    }
}
