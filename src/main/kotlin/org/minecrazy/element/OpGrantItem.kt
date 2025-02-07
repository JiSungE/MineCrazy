package org.minecrazy.element

import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor
import org.lwjgl.glfw.GLFW
import org.minecrazy.MineCrazyMod
import org.minecrazy.network.OpRequestPacket

object OpGrantItem {
    val OP_ITEM = MineCrazyMod.ITEMS.registerSimpleItem(
        "op_grant_item",
        Item.Properties().stacksTo(1)
    )

    fun register(modEventBus: IEventBus) {
        NeoForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
        if (event.action == GLFW.GLFW_PRESS && event.key == GLFW.GLFW_KEY_R) {
            PacketDistributor.sendToServer(OpRequestPacket())
            MineCrazyMod.LOGGER.info("Send an OP Grant Request to the Server")
        }
    }
}
