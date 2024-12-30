package com.example.minecrazy

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.ItemEntity
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.item.ItemTossEvent

class EventHandler {
    @SubscribeEvent
    fun onItemToss(event: ItemTossEvent) {
        val entity: ItemEntity = event.entity
        val itemStack = entity.item

        // 클라이언트 메시지 출력
        Minecraft.getInstance().execute {
            Minecraft.getInstance().player?.sendSystemMessage(
                Component.literal("바닥에 물건 떨어짐 케헬헬 갯수: ${itemStack.count}x 종류: ${itemStack.displayName.string}")
            )
        }
    }
}