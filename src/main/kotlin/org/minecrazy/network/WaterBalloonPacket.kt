package org.minecrazy.network

import kotlinx.coroutines.*
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.minecrazy.MineCrazyMod
import org.minecrazy.element.BaseBalloon

class WaterBalloonPacket(val pos: BlockPos) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<WaterBalloonPacket> = TYPE

    companion object {
        private val explosionScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        private val ID = ResourceLocation(MineCrazyMod.MOD_ID, "water_balloon")
        val TYPE: CustomPacketPayload.Type<WaterBalloonPacket> = CustomPacketPayload.Type(ID)

        // 패킷 직렬화/역직렬화 설정
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, WaterBalloonPacket> =
            StreamCodec.of(
                { buf, packet -> buf.writeBlockPos(packet.pos) },  // 직렬화
                { buf -> WaterBalloonPacket(buf.readBlockPos()) }  // 역직렬화
            )

        // 서버에서 처리할 핸들러
        fun handle(packet: WaterBalloonPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player() as? ServerPlayer ?: return@enqueueWork
                val world = player.level()

                // 물풍선 블록 생성
                world.setBlockAndUpdate(packet.pos, BaseBalloon.WATER_BALLOON_BLOCK.get().defaultBlockState())
                MineCrazyMod.LOGGER.info("서버에서 물풍선 블록 생성 위치: {}", packet.pos)

                scheduleExplosion(player.serverLevel(), packet.pos)
            }
        }

        private fun scheduleExplosion(world: ServerLevel, pos: BlockPos) {
            explosionScope.launch {
                delay(3000)

                if (world.getBlockState(pos).block == BaseBalloon.WATER_BALLOON_BLOCK.get()) {
                    world.removeBlock(pos, false)

                    // 폭발 파티클 효과
                    spawnExplosionParticles(world, pos)

                    // 물 튀는 소리
                    world.playSound(
                        null,
                        pos,
                        SoundEvents.GENERIC_SPLASH,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                    )

                    MineCrazyMod.LOGGER.info("물풍선 터짐 -> 위치: {}", pos)
                }
            }
        }

        private fun spawnExplosionParticles(world: ServerLevel, pos: BlockPos) {
            val maxDistance = 3  // 최대 거리
            val step = 0.2       // 파티클 간격

            val directions = listOf(
                Vec3(1.0, 0.0, 0.0),   // +X
                Vec3(-1.0, 0.0, 0.0),  // -X
                Vec3(0.0, 0.0, 1.0),   // +Z
                Vec3(0.0, 0.0, -1.0)   // -Z
            )

            // 각 방향으로 파티클 생성
            for (direction in directions) {
                var distance = 0.0

                while (distance <= maxDistance) {
                    val particlePos = Vec3(
                        pos.x + direction.x * distance + 0.5, // + 0.5 안하면 모서리에 생김
                        pos.y + 0.5,
                        pos.z + direction.z * distance + 0.5
                    )

                    world.sendParticles(
                        ParticleTypes.SPLASH,
                        particlePos.x,
                        particlePos.y,
                        particlePos.z,
                        3,
                        0.0, 0.0, 0.0,
                        0.01
                    )

                    distance += step
                }
            }

            // 중심 파티클 추가
            world.sendParticles(
                ParticleTypes.DRIPPING_WATER,
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                20,
                0.2, 0.2, 0.2,
                0.01
            )
        }
    }
}
