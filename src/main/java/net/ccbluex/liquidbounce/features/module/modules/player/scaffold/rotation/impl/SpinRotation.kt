package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.SPIN_YAW_INCREMENT
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_WRAP_ANGLE
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.RotationMode
import net.ccbluex.liquidbounce.utils.Rotation


class SpinRotation(
    private val getDefaultPitch: () -> Float
) : RotationMode {
    private var spinYaw = 0f
    
    fun resetSpinYaw() {
        spinYaw = 0f
    }
    
    override fun calculateStaticRotation(
        lockRotation: Rotation?,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation {
        spinYaw += SPIN_YAW_INCREMENT
        if (spinYaw > YAW_WRAP_ANGLE) {
            spinYaw -= 360F
        }
        
        val pitch = lockRotation?.pitch ?: getDefaultPitch()
        return Rotation(spinYaw, pitch)
    }
    
    override fun calculateSearchRotation(
        placeRotation: PlaceRotation,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean,
        steps4590: ArrayList<Float>
    ): Rotation {
        spinYaw += SPIN_YAW_INCREMENT
        if (spinYaw > YAW_WRAP_ANGLE) {
            spinYaw -= 360F
        }
        
        return Rotation(spinYaw, placeRotation.rotation.pitch)
    }
}
