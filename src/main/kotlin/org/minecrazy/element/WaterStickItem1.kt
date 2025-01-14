package org.minecrazy.element

import kotlinx.coroutines.*
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.common.NeoForge
import org.lwjgl.glfw.GLFW
import org.minecrazy.MineCrazyMod

object WaterStickItem1 {
    private val explosionScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val WATER_STICK_ITEM = MineCrazyMod.ITEMS.registerSimpleItem(
        "water_stick1",
        Item.Properties().stacksTo(1)
    )

    fun register(modEventBus: IEventBus) {
        NeoForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
        val clientPlayer = Minecraft.getInstance().player ?: return
        val clientWorld = clientPlayer.level()

        if (event.action == GLFW.GLFW_PRESS && event.key == GLFW.GLFW_KEY_R) {
            val heldItem = clientPlayer.getItemInHand(InteractionHand.MAIN_HAND)

            if (heldItem.item == WaterStickItem1.WATER_STICK_ITEM.get()) {
                val pos = BlockPos(clientPlayer.blockPosition())

                // 서버 인스턴스 가져오기
                val server: MinecraftServer? = Minecraft.getInstance().singleplayerServer

                if (server != null) {
                    // 서버 레벨 가져오기
                    val serverWorld = server.overworld()  // 오버월드 기준, 필요시 다른 차원으로 변경 가능 (지옥이나 엔더월드)

                    serverWorld.setBlockAndUpdate(pos, BaseBalloon.WATER_BALLOON_BLOCK.get().defaultBlockState())
                    MineCrazyMod.LOGGER.info("서버에서 물풍선 블록 생성 위치: {}", pos)

                    // 3초 후 자동 폭발
                    scheduleExplosion(serverWorld, pos)
                } else {
                    MineCrazyMod.LOGGER.warn("서버를 찾을 수 없습니다!")
                }
            }
        }
    }

    private fun scheduleExplosion(world: ServerLevel, pos: BlockPos) {
        explosionScope.launch {
            delay(3000)  // 3초 대기

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

                MineCrazyMod.LOGGER.info("물풍선이 터졌습니다! 위치: {}", pos)
            }
        }
    }

    /**
     * 폭발 파티클 효과
     */
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
                    pos.x + direction.x * distance + 0.5,
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
