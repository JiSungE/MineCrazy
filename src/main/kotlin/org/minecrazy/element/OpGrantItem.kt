package org.minecrazy.element

import com.mojang.authlib.GameProfile
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.players.PlayerList
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.common.NeoForge
import org.lwjgl.glfw.GLFW
import org.minecrazy.MineCrazyMod

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
        val player = Minecraft.getInstance().player ?: return
        val server: MinecraftServer? = player.server
        val playerList: PlayerList? = server?.playerList
        val gameProfile: GameProfile = player.gameProfile

        // R키를 눌렀는지 확인
        if (event.action == GLFW.GLFW_PRESS && event.key == GLFW.GLFW_KEY_R) {
            val itemInHand: ItemStack = player.getItemInHand(InteractionHand.MAIN_HAND)

            if (itemInHand.item == OP_ITEM.get()) {
                if (playerList != null && !playerList.isOp(gameProfile)) {
                    playerList.op(gameProfile)
                    player.sendSystemMessage(Component.literal("You have been granted OP permission!"))
                    MineCrazyMod.LOGGER.info("OP rights granted to OP Volume ${player.name .string} automatically.HAN granted!")
                }
            }
        }

    }

}
