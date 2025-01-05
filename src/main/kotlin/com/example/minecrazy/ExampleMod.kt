package com.example.minecrazy

import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.registries.*
import net.neoforged.neoforge.client.event.InputEvent
import org.lwjgl.glfw.GLFW
import java.util.function.Supplier

@Mod(ExampleMod.MOD_ID)
class ExampleMod(modEventBus: IEventBus, modContainer: ModContainer) {

    companion object {
        const val MOD_ID = "minecrazy"
        private val LOGGER = LogUtils.getLogger()

        val BLOCKS = DeferredRegister.createBlocks(MOD_ID)
        val ITEMS = DeferredRegister.createItems(MOD_ID)
        val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)

        val WATER_BALLOON_ITEM = ITEMS.registerSimpleItem(
            "water_balloon",
            Item.Properties().stacksTo(16)
        )

        val EXAMPLE_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = CREATIVE_MODE_TABS.register(
            "example_tab",
            Supplier {
                CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.minecrazy"))
                    .icon { ItemStack(WATER_BALLOON_ITEM.get()) }
                    .displayItems { _, output ->
                        output.accept(WATER_BALLOON_ITEM.get())
                    }
                    .build()
            }
        )
    }

    init {
        modEventBus.addListener(this::commonSetup)

        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)

        NeoForge.EVENT_BUS.register(this)

        modEventBus.addListener(this::addCreative)

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.spec)

        // 키 입력 등록
        NeoForge.EVENT_BUS.register(this)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("물풍선 등록 완료!")
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(WATER_BALLOON_ITEM)
        }
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        LOGGER.info("서버 Starting")
    }

    // R 키 입력 처리
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
        val player = Minecraft.getInstance().player ?: return
        val world = player.level()

        if (event.action == GLFW.GLFW_PRESS && event.key == GLFW.GLFW_KEY_R) {
            val heldItem = player.getItemInHand(InteractionHand.MAIN_HAND)
            if (heldItem.item == WATER_BALLOON_ITEM.get()) {
                val pos = BlockPos(player.blockPosition())
                world.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState())
                LOGGER.info("물풍선 생성 위치: {}", pos)
            }
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {
            LOGGER.info("클라이언트 Starting")
        }
    }
}
