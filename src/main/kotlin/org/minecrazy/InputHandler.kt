package org.minecrazy

import net.neoforged.bus.api.SubscribeEvent
import org.lwjgl.glfw.GLFW
import net.neoforged.neoforge.client.event.InputEvent

object InputHandler {
    private val keyActions = mutableMapOf<Int, KeyAction>()

    init {
        keyActions[GLFW.GLFW_KEY_R] = PlaceAAction()
        keyActions[GLFW.GLFW_KEY_T] = PlaceBAction()
    }

    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
        if (event.action == GLFW.GLFW_PRESS) {
            keyActions[event.key]?.execute()
        }
    }
}