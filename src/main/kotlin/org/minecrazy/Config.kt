package org.minecrazy

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec
// 예제에 있는 Config임
@EventBusSubscriber(modid = MineCrazyMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object Config {

    private val builder = ModConfigSpec.Builder()

    private val logDirtBlockSpec: ModConfigSpec.BooleanValue = builder
        .comment("Whether to log the dirt block on common setup")
        .define("logDirtBlock", true)

    private val magicNumberSpec: ModConfigSpec.IntValue = builder
        .comment("A magic number")
        .defineInRange("magicNumber", 42, 0, Int.MAX_VALUE)

    val magicNumberIntroductionSpec: ModConfigSpec.ConfigValue<String> = builder
        .comment("What you want the introduction message to be for the magic number")
        .define("magicNumberIntroduction", "The magic number is... ")

    private val itemStringsSpec: ModConfigSpec.ConfigValue<List<String>> = builder
        .comment("A list of items to log on common setup.")
        .defineListAllowEmpty("items", listOf("minecraft:iron_ingot")) { obj ->
            obj is String && BuiltInRegistries.ITEM.containsKey(ResourceLocation(obj))
        }

    val spec: ModConfigSpec = builder.build()

    var logDirtBlock: Boolean = true
    var magicNumber: Int = 42
    var magicNumberIntroduction: String = "The magic number is... "
    lateinit var items: Set<Item>

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        logDirtBlock = logDirtBlockSpec.get()
        magicNumber = magicNumberSpec.get()
        magicNumberIntroduction = magicNumberIntroductionSpec.get()

        // Convert the list of strings into a set of items
        items = itemStringsSpec.get().mapNotNull { itemName ->
            BuiltInRegistries.ITEM.get(ResourceLocation(itemName))
        }.toSet()
    }
}
