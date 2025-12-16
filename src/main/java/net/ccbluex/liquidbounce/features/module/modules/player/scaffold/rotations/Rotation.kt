package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.Rotation

abstract class ScaffoldRotation(val modeName: String) : MinecraftInstance() {
    abstract fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation?
    open fun onEnable() {}
    open fun onDisable() {}
}

