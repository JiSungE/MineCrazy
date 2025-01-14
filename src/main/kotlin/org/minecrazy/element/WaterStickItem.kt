package org.minecrazy.element

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.common.NeoForge
import org.lwjgl.glfw.GLFW
import org.minecrazy.MineCrazyMod
import org.minecrazy.balloon.WaterBalloon
import org.minecrazy.network.WaterBalloonPacket

object WaterStickItem {

    val WATER_STICK_ITEM = MineCrazyMod.ITEMS.registerSimpleItem(
        "water_stick",
        Item.Properties().stacksTo(1)
    )

    fun register(modEventBus: IEventBus) {
        NeoForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
        val clientPlayer = Minecraft.getInstance().player ?: return

        if (event.action == GLFW.GLFW_PRESS && event.key == GLFW.GLFW_KEY_R) {
            val heldItem = clientPlayer.getItemInHand(InteractionHand.MAIN_HAND)

            if (heldItem.item == WATER_STICK_ITEM.get()) {
                val pos = BlockPos(clientPlayer.blockPosition())

                // 서버에 패킷 전송
                PacketDistributor.sendToServer(WaterBalloonPacket(pos))
                MineCrazyMod.LOGGER.info("서버로 물풍선 패킷 전송: {}", pos)
            }
        }
    }
}
