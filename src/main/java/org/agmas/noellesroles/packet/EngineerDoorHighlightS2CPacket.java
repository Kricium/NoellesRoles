package org.agmas.noellesroles.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.agmas.noellesroles.Noellesroles;

/**
 * 工程师门高亮 S2C 数据包
 * 服务端在门被撬/被堵时发送给工程师客户端，触发5秒红色透视描边
 */
public record EngineerDoorHighlightS2CPacket(BlockPos doorPos) implements CustomPayload {
    public static final CustomPayload.Id<EngineerDoorHighlightS2CPacket> ID =
            new CustomPayload.Id<>(Identifier.of(Noellesroles.MOD_ID, "engineer_door_highlight"));

    public static final PacketCodec<RegistryByteBuf, EngineerDoorHighlightS2CPacket> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, EngineerDoorHighlightS2CPacket::doorPos,
                    EngineerDoorHighlightS2CPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
