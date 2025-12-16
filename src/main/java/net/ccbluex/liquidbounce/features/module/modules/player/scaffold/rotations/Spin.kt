package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.Rotation

class Spin : ScaffoldRotation("Spin") {
    private var spinYaw = 0f

    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation {
        spinYaw += 45F
        if (spinYaw > 180F) {
            spinYaw -= 360F
        }
        val pitch = lockRotation?.pitch ?: defaultPitch
        return Rotation(spinYaw, pitch)
    }

    override fun onEnable() {
        spinYaw = 0f
    }
}
