package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

import net.ccbluex.liquidbounce.utils.MinecraftInstance

abstract class Sprint(val modeName: String) : MinecraftInstance() {
    abstract fun isActive(): Boolean
    open fun onTick() {}
}
