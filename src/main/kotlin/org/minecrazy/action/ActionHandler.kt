package org.minecrazy.action

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import org.lwjgl.glfw.GLFW

object ActionHandler {
    private val KeyActions = mutableMapOf<Int, IAction>()

    init {
        KeyActions[GLFW.GLFW_KEY_R];
    }

    @SubscribeEvent
    fun OnKeyInput(event: InputEvent.Key) {
        if(event.action == GLFW.GLFW_PRESS) {
            KeyActions[event.key]?.execute(event)
        }
    }
}