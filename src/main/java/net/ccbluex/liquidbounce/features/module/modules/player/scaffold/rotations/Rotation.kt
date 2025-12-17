package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.block.PlaceInfo

abstract class ScaffoldRotation(val modeName: String) : MinecraftInstance() {
    protected var currentTargetPlace: PlaceInfo? = null

    abstract fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation?

    open fun setTargetPlace(targetPlace: PlaceInfo?) {
        currentTargetPlace = targetPlace
    }

    open fun onEnable() {}
    open fun onDisable() {}
}
