package com.example.minecrazy

import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge

@Mod(DropItemLogger.MOD_ID)
object DropItemLogger {
    const val MOD_ID = "minecrazy"

    init {
        NeoForge.EVENT_BUS.register(EventHandler())
    }
}