package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.UUID;

public record HallucinationDummyUseC2SPacket(UUID dummyId, HallucinationDummyUseAction action) implements CustomPayload {
    public static final Id<HallucinationDummyUseC2SPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "hallucination_dummy_use"));

    public static final PacketCodec<RegistryByteBuf, HallucinationDummyUseC2SPacket> CODEC =
            PacketCodec.of(HallucinationDummyUseC2SPacket::write, HallucinationDummyUseC2SPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.dummyId);
        buf.writeEnumConstant(this.action);
    }

    private static HallucinationDummyUseC2SPacket read(PacketByteBuf buf) {
        return new HallucinationDummyUseC2SPacket(buf.readUuid(), buf.readEnumConstant(HallucinationDummyUseAction.class));
    }
}
