package org.minecrazy.network

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

object NetworkHandler {
    fun registerPackets(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1.0.0")  // 네트워크 버전 필수 (왜 필요한지는 몰?루)

        // 서버로 전송하는 패킷 등록
        registrar.playToServer(
            WaterBalloonPacket.TYPE,   // 패킷 타입
            WaterBalloonPacket.CODEC,  // 직렬화/역직렬화 방식
            WaterBalloonPacket::handle // 패킷 핸들러
        )

        registrar.playToServer(
            OpRequestPacket.TYPE,
            OpRequestPacket.CODEC,
            OpRequestPacket::handle
        )
    }
}
