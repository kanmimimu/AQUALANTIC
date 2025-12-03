package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_STEP_45
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_WRAP_ANGLE
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.RotationMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import kotlin.math.round

/**
 * Stabilized ローテーションモード
 * 45度の倍数に安定化された回転
 */
class StabilizedRotation(
    private val getDefaultPitch: () -> Float
) : RotationMode {
    override fun calculateStaticRotation(
        lockRotation: Rotation?,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation {
        val yaw = lockRotation?.yaw ?: (MovementUtils.movingYaw + YAW_WRAP_ANGLE)
        val pitch = lockRotation?.pitch ?: getDefaultPitch()
        return Rotation(yaw, pitch)
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
        val fixYaw = round(placeRotation.rotation.yaw / YAW_STEP_45) * YAW_STEP_45
        val stabilized = if (fixYaw in steps4590) fixYaw else MovementUtils.movingYaw + steps4590[0]
        
        return Rotation(stabilized, placeRotation.rotation.pitch)
    }
}
