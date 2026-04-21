package org.agmas.noellesroles.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

public record DeathArenaToggleC2SPacket() implements CustomPayload {
    public static final Id<DeathArenaToggleC2SPacket> ID =
            new Id<>(Identifier.of(Noellesroles.MOD_ID, "death_arena_toggle"));

    public static final PacketCodec<RegistryByteBuf, DeathArenaToggleC2SPacket> CODEC =
            PacketCodec.of(DeathArenaToggleC2SPacket::write, DeathArenaToggleC2SPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
    }

    private static DeathArenaToggleC2SPacket read(PacketByteBuf buf) {
        return new DeathArenaToggleC2SPacket();
    }
}
