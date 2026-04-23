package org.agmas.noellesroles.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

public record HallucinationSoundS2CPacket(String soundId) implements CustomPayload {
    public static final Id<HallucinationSoundS2CPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "hallucination_sound"));

    public static final PacketCodec<RegistryByteBuf, HallucinationSoundS2CPacket> CODEC =
            PacketCodec.of(HallucinationSoundS2CPacket::write, HallucinationSoundS2CPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(RegistryByteBuf buf) {
        buf.writeString(this.soundId);
    }

    private static HallucinationSoundS2CPacket read(RegistryByteBuf buf) {
        return new HallucinationSoundS2CPacket(buf.readString());
    }
}
