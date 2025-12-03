package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_STEP_135
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_STEP_45
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_WRAP_ANGLE
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.RotationMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import kotlin.math.abs

/**
 * WatchDog2 ローテーションモード
 * WatchDogサーバー用の代替ローテーション
 */
class WatchDog2Rotation(
    private val getDefaultPitch: () -> Float
) : RotationMode {
    
    private fun isDiagonal(yaw: Float): Boolean {
        val absYaw = abs(yaw % 90.0F)
        return absYaw > 20.0F && absYaw < 70.0F
    }
    
    override fun calculateStaticRotation(
        lockRotation: Rotation?,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation {
        val currentYaw = MovementUtils.movingYaw
        val yawDiffTo180 = currentYaw - YAW_WRAP_ANGLE
        val diagonalYaw = if (isDiagonal(currentYaw)) {
            yawDiffTo180
        } else {
            currentYaw - YAW_STEP_135 * (if ((currentYaw + YAW_WRAP_ANGLE) % 90.0F < YAW_STEP_45) 1.0F else -1.0F)
        }
        
        val pitch = lockRotation?.pitch ?: getDefaultPitch()
        return Rotation(lockRotation?.yaw ?: diagonalYaw, pitch)
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
        val currentYaw = MovementUtils.movingYaw
        val yawDiffTo180 = currentYaw - YAW_WRAP_ANGLE
        val diagonalYaw = if (isDiagonal(currentYaw)) {
            yawDiffTo180
        } else {
            currentYaw - YAW_STEP_135 * (if ((currentYaw + YAW_WRAP_ANGLE) % 90.0F < YAW_STEP_45) 1.0F else -1.0F)
        }
        
        return Rotation(diagonalYaw, placeRotation.rotation.pitch)
    }
}
