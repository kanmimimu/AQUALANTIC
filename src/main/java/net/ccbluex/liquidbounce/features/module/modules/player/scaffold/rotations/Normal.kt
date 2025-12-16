package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.Rotation

class Normal : ScaffoldRotation("Normal") {
    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation? {
        return lockRotation
    }
}
