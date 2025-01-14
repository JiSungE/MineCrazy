package org.minecrazy.action

import net.neoforged.neoforge.client.event.InputEvent

interface IAction {
    fun execute(event: InputEvent.Key)
}