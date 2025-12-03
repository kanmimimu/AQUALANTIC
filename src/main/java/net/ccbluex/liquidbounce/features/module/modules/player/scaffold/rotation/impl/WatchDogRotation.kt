package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_WRAP_ANGLE
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation.RotationMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.Rotation

/**
 * WatchDog ローテーションモード
 * WatchDogサーバー用のローテーション
 */
class WatchDogRotation(
    private val watchdogTellyValue: () -> Boolean,
    private val watchDogDelay: () -> Int,
    private val watchdogBoostValue: () -> Boolean,
    private val keyBindUseItem: () -> Boolean,
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
        val shouldUseTellyYaw = watchDogDelay() > PlayerUtils.offGroundTicks && 
            !towerStatus && 
            (!watchdogBoostValue() || !keyBindUseItem()) &&
            bridgeMode == "WatchDog" && 
            watchdogTellyValue()
            
        val yaw = if (shouldUseTellyYaw) {
            MovementUtils.movingYaw
        } else {
            lockRotation?.yaw ?: (MovementUtils.movingYaw - YAW_WRAP_ANGLE)
        }
        
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
        val shouldUseTellyYaw = watchDogDelay() > PlayerUtils.offGroundTicks && 
            !towerStatus && 
            (!watchdogBoostValue() || !keyBindUseItem()) &&
            watchdogTellyValue() && 
            bridgeMode == "WatchDog"
            
        val yaw = if (shouldUseTellyYaw) {
            MovementUtils.movingYaw
        } else {
            MovementUtils.movingYaw - YAW_WRAP_ANGLE
        }
        
        return Rotation(yaw, placeRotation.rotation.pitch)
    }
}
