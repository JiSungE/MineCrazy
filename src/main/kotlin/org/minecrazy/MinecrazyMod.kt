package org.minecrazy

import com.mojang.logging.LogUtils
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.*
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
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.*
import org.minecrazy.element.*
import org.minecrazy.network.NetworkHandler

@Mod(MineCrazyMod.MOD_ID)
class MineCrazyMod(modEventBus: IEventBus, modContainer: ModContainer) {

    companion object {
        const val MOD_ID = "minecrazy"
        val LOGGER = LogUtils.getLogger()

        val BLOCKS = DeferredRegister.createBlocks(MOD_ID)
        val ITEMS = DeferredRegister.createItems(MOD_ID)
        val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)
    }

    init {
        modEventBus.addListener(this::commonSetup)
        modEventBus.addListener(this::addCreative)
        modEventBus.addListener(this::onRegisterPayload)

        // DeferredRegister 등록
        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)

        // 각각의 객체가 자체적으로 관리하는 기능들을 Setup
        WaterStickItem.register(modEventBus)
        WaterStickItem1.register(modEventBus)
        OpGrantItem.register(modEventBus)
        BaseBalloon.register(modEventBus)
        MIneCrazyTab.register(modEventBus)

        NeoForge.EVENT_BUS.register(this)


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.spec)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("물풍선 등록 완료!")
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(WaterStickItem.WATER_STICK_ITEM)
            event.accept(WaterStickItem1.WATER_STICK_ITEM)
            event.accept(OpGrantItem.OP_ITEM)
            event.accept(BaseBalloon.BASE_BALLOON_BLOCK_ITEM)
        }
    }

    fun onRegisterPayload(event: RegisterPayloadHandlersEvent) {
        NetworkHandler.registerPackets(event)
        LOGGER.info("네트워크 패킷이 등록되었습니다.")
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        LOGGER.info("서버 Starting")
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {
            LOGGER.info("클라이언트 Starting")
        }
    }
}
