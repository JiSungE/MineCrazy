package org.minecrazy.balloon

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
import net.minecraft.util.RandomSource
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.apache.logging.log4j.core.jmx.Server
import org.minecrazy.MineCrazyMod
import org.minecrazy.network.WaterBalloonPacket
import org.minecrazy.network.WaterBalloonPacket.Companion.TYPE
import java.util.function.Supplier

class WaterBalloon(properties: Properties) : Block(properties)//, CustomPacketPayload {
{
    //override fun type(): CustomPacketPayload.Type<WaterBalloonPacket> = TYPE

    /*class Packet(val pos: BlockPos) : CustomPacketPayload {
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
                    world.setBlockAndUpdate(packet.pos, BALLOON_BLOCK.get().defaultBlockState())
                    MineCrazyMod.LOGGER.info("서버에서 물풍선 블록 생성 위치: {}", packet.pos)

                    scheduleExplosion(player.serverLevel(), packet.pos)
                }
            }

            private fun scheduleExplosion(world: ServerLevel, pos: BlockPos) {
                explosionScope.launch {
                    delay(3000)

                    if (world.getBlockState(pos).block == BALLOON_BLOCK.get()) {
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
    }*/

    companion object {
        val block_name = "water_balloon"

        val BALLOON_BLOCK = MineCrazyMod.BLOCKS.register(
            block_name,
            Supplier {
                WaterBalloon(
                    Properties.of()
                )
            }
        )

        val BALLOON_BLOCK_ITEM = MineCrazyMod.ITEMS.register(
            block_name,
            Supplier {
                BlockItem(BALLOON_BLOCK.get(), Item.Properties())
            }
        )

        fun register(modEventBus: IEventBus) {
            // TODO: 추후 자체적으로 관리하는 아이템 및 블록 등록하는 코드임
        }
    }

    override fun onPlace(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pOldState: BlockState,
        pMovedByPiston: Boolean
    ) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston)

        /*if(!pLevel.isClientSide()) {
            (pLevel as ServerLevel).scheduleTick(pPos, this, 20)
        }*/
        pLevel.scheduleTick(pPos, this, 20)
    }

    override fun tick(
        pState: BlockState,
        pLevel: ServerLevel,
        pPos: BlockPos,
        pRandom: RandomSource
    ) {
        super.tick(pState, pLevel, pPos, pRandom)

        // 폭발 실행
        if (!pLevel.isClientSide) {
            pLevel.explode(
                null,
                pPos.x + 0.5,
                pPos.y + 0.5,
                pPos.z + 0.5,
                4.0f,
                Level.ExplosionInteraction.BLOCK
            )

            pLevel.removeBlock(pPos, false)
        }
    }

    override fun onBlockExploded(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        explosion: Explosion
    ) {
        super.onBlockExploded(state, level, pos, explosion)

        MineCrazyMod.LOGGER.info("Boooooooooooom")
    }
}