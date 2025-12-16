package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation

class Stabilized : ScaffoldRotation("Stabilized") {
    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation {
        val yaw = lockRotation?.yaw ?: (MovementUtils.movingYaw + 180)
        val pitch = lockRotation?.pitch ?: defaultPitch
        return Rotation(yaw, pitch)
    }
}
