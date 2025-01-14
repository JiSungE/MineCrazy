package org.minecrazy.element

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import org.minecrazy.MineCrazyMod
import org.minecrazy.balloon.WaterBalloon
import java.util.function.Supplier

object MIneCrazyTab {
    /*val MINECRAZY_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = MineCrazyMod.CREATIVE_MODE_TABS.register(
        "minecrazy_tab",
        Supplier {
            CreativeModeTab.builder()
                .title(Component.translatable("minecrazy"))
                .icon { ItemStack(WaterStickItem.WATER_STICK_ITEM.get()) }
                .displayItems { _, output ->
                    output.accept(WaterStickItem.WATER_STICK_ITEM.get())
                    output.accept(BaseBalloon.BASE_BALLOON_BLOCK_ITEM.get())
                }
                .build()
        }
    )*/

    val MINECRAZY_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = MineCrazyMod.CREATIVE_MODE_TABS.register(
        "minecrazy_tab",
        Supplier {
            CreativeModeTab.builder()
                .title(Component.translatable("minecrazy"))
                .icon { ItemStack(WaterStickItem.WATER_STICK_ITEM.get()) }
                .displayItems { _, output ->
                    output.accept(WaterStickItem.WATER_STICK_ITEM.get())
                    output.accept(WaterBalloon.BALLOON_BLOCK_ITEM.get())
                }
                .build()
        }
    )

    fun register(modEventBus: IEventBus) {
        // TODO: 추후 자체적으로 관리하는 아이템 및 블록 등록하는 코드임
    }
}