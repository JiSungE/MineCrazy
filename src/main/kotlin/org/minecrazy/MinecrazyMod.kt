package org.minecrazy

import com.mojang.logging.LogUtils
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge

@Mod(MinecrazyMod.MOD_ID)
class MinecrazyMod {
    companion object {
        const val MOD_ID = "minecrazy"
    }

    init {
        NeoForge.EVENT_BUS.register(InputHandler)
    }
}