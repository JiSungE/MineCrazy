package org.minecrazy

import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft

class PlaceAAction : KeyAction {
    private val LOGGER = LogUtils.getLogger()

    override fun execute() {
        val player = Minecraft.getInstance().player ?: return
        val world = player.level()

        LOGGER.info("Action A Key")
    }
}