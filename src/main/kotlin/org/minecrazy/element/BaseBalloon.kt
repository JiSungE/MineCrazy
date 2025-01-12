package org.minecrazy.element

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.neoforged.bus.api.IEventBus
import org.minecrazy.MineCrazyMod
import java.util.function.Supplier

object BaseBalloon {
    val WATER_BALLOON_BLOCK = MineCrazyMod.BLOCKS.register(
        "water_balloon_block",
        Supplier {
            Block(
                BlockBehaviour.Properties.of()
                    .strength(1.0f)
                    .mapColor(MapColor.COLOR_BLUE)
            )
        }
    )

    val BASE_BALLOON_BLOCK_ITEM = MineCrazyMod.ITEMS.register(
        "water_balloon_block",
        Supplier {
            net.minecraft.world.item.BlockItem(WATER_BALLOON_BLOCK.get(), net.minecraft.world.item.Item.Properties())
        }
    )

    fun register(modEventBus: IEventBus) {
        // TODO: 추후 자체적으로 관리하는 아이템 및 블록 등록하는 코드임
    }
}
