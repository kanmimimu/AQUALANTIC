package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation

class Telly(
    private val prevToweredProvider: () -> Boolean,
    private val shouldPlaceProvider: () -> Boolean
) : ScaffoldRotation("Telly") {
    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation {
        val yaw = when {
            prevToweredProvider() -> lockRotation?.yaw ?: (MovementUtils.movingYaw - 180)
            !shouldPlaceProvider() -> MovementUtils.movingYaw
            else -> lockRotation?.yaw ?: (MovementUtils.movingYaw - 180)
        }
        val pitch = lockRotation?.pitch ?: defaultPitch
        return Rotation(yaw, pitch)
    }
}

