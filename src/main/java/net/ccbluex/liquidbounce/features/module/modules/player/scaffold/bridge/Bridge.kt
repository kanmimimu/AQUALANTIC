package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.util.BlockPos

abstract class Bridge(val modeName: String) : MinecraftInstance() {
    abstract fun onTick()
    abstract fun getBlockPosition(lastGroundY: Int): BlockPos
    open fun shouldPlace(): Boolean = true
    open fun onEnable() {}
}
