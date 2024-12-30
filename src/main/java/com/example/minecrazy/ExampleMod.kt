package com.example.minecrazy

import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT

@Mod(ExampleMod.MOD_ID)
object ExampleMod {
    const val MOD_ID = "minecrazy"

    init {
        // MOD_CONTEXT 이벤트 버스
        val modEventBus = MOD_CONTEXT.getKEventBus()

        // 공통 설정
        modEventBus.addListener(::setupCommon)

        // 클라이언트 설정
        modEventBus.addListener(::setupClient)
    }

    // 공통 설정
    private fun setupCommon(event: FMLCommonSetupEvent) {
        println("$MOD_ID: 공통 설정")
    }

    // 클라이언트 설정
    private fun setupClient(event: FMLClientSetupEvent) {
        println("$MOD_ID: 클라이언트 설정")
    }
}
