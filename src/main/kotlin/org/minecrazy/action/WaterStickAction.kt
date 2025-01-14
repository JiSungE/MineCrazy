package org.minecrazy.action

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.lwjgl.glfw.GLFW
import org.minecrazy.MineCrazyMod
import org.minecrazy.element.WaterStickItem.WATER_STICK_ITEM
import org.minecrazy.network.WaterBalloonPacket

class WaterStickAction : IAction {
    override fun execute(event: InputEvent.Key) {
        val clientPlayer = Minecraft.getInstance().player ?: return

        if (event.key == GLFW.GLFW_KEY_R) {
            val heldItem = clientPlayer.getItemInHand(InteractionHand.MAIN_HAND)

            if (heldItem.item == WATER_STICK_ITEM.get()) {
                val pos = BlockPos(clientPlayer.blockPosition())

                PacketDistributor.sendToServer(WaterBalloonPacket(pos))
                MineCrazyMod.LOGGER.info("서버로 물풍선 패킷 전송: {}", pos)
            }
        }
    }
}