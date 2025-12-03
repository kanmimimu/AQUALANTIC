package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_WRAP_ANGLE
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.RotationMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation

/**
 * Snap ローテーションモード
 * 移動方向から180度反転した方向にスナップ
 */
class SnapRotation : RotationMode {
    override fun calculateStaticRotation(
        lockRotation: Rotation?,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation? {
        return null
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
        val yaw = MovementUtils.movingYaw + YAW_WRAP_ANGLE
        return Rotation(yaw, placeRotation.rotation.pitch)
    }
}
