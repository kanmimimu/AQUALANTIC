package net.ccbluex.liquidbounce.features.module.modules.player.scaffold

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.DEFAULT_ROTATION_PITCH
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.DIRECTION_VEC_MULTIPLIER
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.RAY_TRACE_MULTIPLIER
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.SPIN_YAW_INCREMENT
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_DIAGONAL_MAX
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_DIAGONAL_MIN
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_STEP_135
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_STEP_45
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.YAW_WRAP_ANGLE
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.block.PlaceInfo
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.round

data class PlaceRotation(val placeInfo: PlaceInfo, val rotation: Rotation)

object ScaffoldRotationManager {
    private var spinYaw = 0f
    
    fun resetSpinYaw() {
        spinYaw = 0f
    }

    fun calculateRotationForMode(
        mode: String,
        placeRotation: PlaceRotation?,
        lockRotation: Rotation?,
        bridgeMode: String,
        isLookingDiagonally: Boolean,
        towerStatus: Boolean,
        watchdogTellyValue: Boolean,
        watchDogDelay: Int,
        watchdogBoostValue: Boolean,
        keyBindUseItem: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation? {
        val pitch = lockRotation?.pitch ?: DEFAULT_ROTATION_PITCH
        
        return when (mode.lowercase()) {
            "stabilized" -> {
                val yaw = lockRotation?.yaw ?: (MovementUtils.movingYaw + YAW_WRAP_ANGLE)
                Rotation(yaw, pitch)
            }
            
            "watchdog" -> {
                val shouldUseTellyYaw = watchDogDelay > PlayerUtils.offGroundTicks && 
                    !towerStatus && 
                    (!watchdogBoostValue || !keyBindUseItem) &&
                    bridgeMode == "WatchDog" && 
                    watchdogTellyValue
                val yaw = if (shouldUseTellyYaw) {
                    MovementUtils.movingYaw
                } else {
                    lockRotation?.yaw ?: (MovementUtils.movingYaw - YAW_WRAP_ANGLE)
                }
                Rotation(yaw, pitch)
            }
            
            "watchdog2" -> {
                val currentYaw = MovementUtils.movingYaw
                val yawDiffTo180 = currentYaw - YAW_WRAP_ANGLE
                val diagonalYaw = if (isDiagonal(currentYaw)) {
                    yawDiffTo180
                } else {
                    currentYaw - YAW_STEP_135 * (if ((currentYaw + YAW_WRAP_ANGLE) % 90.0F < YAW_STEP_45) 1.0F else -1.0F)
                }
                Rotation(lockRotation?.yaw ?: diagonalYaw, pitch)
            }
            
            "telly" -> {
                val rotationYaw = when {
                    prevTowered -> lockRotation?.yaw ?: (MovementUtils.movingYaw - YAW_WRAP_ANGLE)
                    !shouldPlace -> MovementUtils.movingYaw
                    else -> lockRotation?.yaw ?: (MovementUtils.movingYaw - YAW_WRAP_ANGLE)
                }
                Rotation(rotationYaw, lockRotation?.pitch ?: DEFAULT_ROTATION_PITCH)
            }
            
            "spin" -> {
                spinYaw += SPIN_YAW_INCREMENT
                if (spinYaw > YAW_WRAP_ANGLE) {
                    spinYaw -= 360F
                }
                Rotation(spinYaw, pitch)
            }
            
            else -> null
        }
    }

    fun calculateSearchRotation(
        mode: String,
        placeRotation: PlaceRotation,
        bridgeMode: String,
        isLookingDiagonally: Boolean,
        towerStatus: Boolean,
        watchdogTellyValue: Boolean,
        watchDogDelay: Int,
        watchdogBoostValue: Boolean,
        keyBindUseItem: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean,
        steps4590: ArrayList<Float>
    ): Rotation {
        val fixYaw = round(placeRotation.rotation.yaw / YAW_STEP_45) * YAW_STEP_45
        val stabilized = if (fixYaw in steps4590) fixYaw else MovementUtils.movingYaw + steps4590[0]
        
        return when (mode.lowercase()) {
            "normal" -> placeRotation.rotation
            
            "stabilized" -> Rotation(stabilized, placeRotation.rotation.pitch)
            
            "watchdog" -> {
                val shouldUseTellyYaw = watchDogDelay > PlayerUtils.offGroundTicks && 
                    !towerStatus && 
                    (!watchdogBoostValue || !keyBindUseItem) &&
                    watchdogTellyValue && 
                    bridgeMode == "WatchDog"
                val yaw = if (shouldUseTellyYaw) {
                    MovementUtils.movingYaw
                } else {
                    MovementUtils.movingYaw - YAW_WRAP_ANGLE
                }
                Rotation(yaw, placeRotation.rotation.pitch)
            }
            
            "watchdog2" -> {
                val currentYaw = MovementUtils.movingYaw
                val yawDiffTo180 = currentYaw - YAW_WRAP_ANGLE
                val diagonalYaw = if (isDiagonal(currentYaw)) {
                    yawDiffTo180
                } else {
                    currentYaw - YAW_STEP_135 * (if ((currentYaw + YAW_WRAP_ANGLE) % 90.0F < YAW_STEP_45) 1.0F else -1.0F)
                }
                Rotation(diagonalYaw, placeRotation.rotation.pitch)
            }
            
            "telly" -> {
                val yaw = when {
                    prevTowered -> fixYaw
                    !shouldPlace -> MovementUtils.movingYaw
                    else -> fixYaw
                }
                Rotation(yaw, placeRotation.rotation.pitch)
            }
            
            "snap" -> Rotation(MovementUtils.movingYaw + YAW_WRAP_ANGLE, placeRotation.rotation.pitch)
            
            "spin" -> {
                spinYaw += SPIN_YAW_INCREMENT
                if (spinYaw > YAW_WRAP_ANGLE) {
                    spinYaw -= 360F
                }
                Rotation(spinYaw, placeRotation.rotation.pitch)
            }
            
            else -> Rotation(0f, 0f)
        }
    }

    fun calculateRotation(eyesPos: Vec3, hitVec: Vec3): Rotation {
        val diffX = hitVec.xCoord - eyesPos.xCoord
        val diffY = hitVec.yCoord - eyesPos.yCoord
        val diffZ = hitVec.zCoord - eyesPos.zCoord
        val diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()

        return Rotation(
            MathHelper.wrapAngleTo180_float(Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90f),
            MathHelper.wrapAngleTo180_float(-Math.toDegrees(atan2(diffY, diffXZ)).toFloat())
        )
    }

    fun isValidBlockRotation(neighbor: BlockPos, eyesPos: Vec3, rotation: Rotation, mc: net.minecraft.client.Minecraft): Boolean {
        val rotationVector = RotationUtils.getVectorForRotation(rotation)
        val vector = eyesPos.addVector(
            rotationVector.xCoord * RAY_TRACE_MULTIPLIER, 
            rotationVector.yCoord * RAY_TRACE_MULTIPLIER, 
            rotationVector.zCoord * RAY_TRACE_MULTIPLIER
        )
        val obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true)
        return obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.blockPos == neighbor
    }

    fun generateOffsets(): Sequence<Vec3> = sequence {
        var x = ScaffoldConstants.OFFSET_STEP
        while (x < ScaffoldConstants.OFFSET_MAX) {
            var y = ScaffoldConstants.OFFSET_STEP
            while (y < ScaffoldConstants.OFFSET_MAX) {
                var z = ScaffoldConstants.OFFSET_STEP
                while (z < ScaffoldConstants.OFFSET_MAX) {
                    yield(Vec3(x, y, z))
                    z += ScaffoldConstants.OFFSET_STEP
                }
                y += ScaffoldConstants.OFFSET_STEP
            }
            x += ScaffoldConstants.OFFSET_STEP
        }
    }

    private fun isDiagonal(yaw: Float): Boolean {
        val absYaw = abs(yaw % 90.0F)
        return absYaw > YAW_DIAGONAL_MIN && absYaw < YAW_DIAGONAL_MAX
    }
}
