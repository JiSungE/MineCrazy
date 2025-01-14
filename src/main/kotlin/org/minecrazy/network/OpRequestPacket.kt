package org.minecrazy.network

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.minecraft.server.MinecraftServer
import net.minecraft.server.players.PlayerList
import org.minecrazy.MineCrazyMod
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
//  net. minecraft. network. protocol. common. custom
class OpRequestPacket : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<*> = TYPE

    companion object {
        // 패킷 ID 설정
        private val ID = ResourceLocation(MineCrazyMod.MOD_ID, "op_grant_item")

        // 패킷 타입 등록
        val TYPE: CustomPacketPayload.Type<OpRequestPacket> = CustomPacketPayload.Type(ID)

        // 직렬화 및 역직렬화 (데이터가 없으므로 비워둠)
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, OpRequestPacket> =
            StreamCodec.of(
                { _, _ -> },  // 직렬화(보낼 때)
                { _ -> OpRequestPacket() }  // 역직렬화(받을 때)
            )

        // 패킷 핸들러
        fun handle(packet: OpRequestPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player() as? ServerPlayer ?: return@enqueueWork
                val server: MinecraftServer = player.server
                val playerList: PlayerList = server.playerList

                // OP 권한 부여
                if (!playerList.isOp(player.gameProfile)) {
                    playerList.op(player.gameProfile)
                    player.sendSystemMessage(Component.literal("You have been granted OP permission!"))
                }
            }
        }
    }
}