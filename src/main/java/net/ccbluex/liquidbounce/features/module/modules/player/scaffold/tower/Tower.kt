package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.MinecraftInstance

abstract class Tower(val modeName: String) : MinecraftInstance() {
    abstract fun onMove()
    open fun onEnable() {}
}
