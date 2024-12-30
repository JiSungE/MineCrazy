package com.example.minecrazy

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec

@EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object Config {

    private val BUILDER = ModConfigSpec.Builder()

    val logDirtBlock: ModConfigSpec.BooleanValue = BUILDER
        .comment("Whether to log the dirt block on common setup")
        .define("logDirtBlock", true)

    val magicNumber: ModConfigSpec.IntValue = BUILDER
        .comment("A magic number")
        .defineInRange("magicNumber", 42, 0, Int.MAX_VALUE)

    val magicNumberIntroduction: ModConfigSpec.ConfigValue<String> = BUILDER
        .comment("Magic number introduction message")
        .define("magicNumberIntroduction", "The magic number is... ")

    val spec: ModConfigSpec = BUILDER.build()

    private fun validateItemName(obj: Any): Boolean {
        return obj is String && BuiltInRegistries.ITEM.containsKey(ResourceLocation(obj))
    }

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        val dirtBlock = logDirtBlock.get()
        val magic = magicNumber.get()
        val intro = magicNumberIntroduction.get()
    }
}
