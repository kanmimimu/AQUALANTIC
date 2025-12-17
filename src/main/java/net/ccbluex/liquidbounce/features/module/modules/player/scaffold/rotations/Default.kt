package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.block.PlaceInfo
import net.ccbluex.liquidbounce.utils.extensions.rayTraceWithCustomRotation
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.math.atan2

class Default : ScaffoldRotation("Default") {
    
    companion object {
        private val PLACE_OFFSETS = doubleArrayOf(
            0.03125, 0.09375, 0.15625, 0.21875,
            0.28125, 0.34375, 0.40625, 0.46875,
            0.53125, 0.59375, 0.65625, 0.71875,
            0.78125, 0.84375, 0.90625, 0.96875
        )
    }

    private var yaw = -180.0f
    private var pitch = 0.0f
    private var canRotate = false
    private var lastBlockPos: BlockPos? = null

    override fun onEnable() {
        yaw = -180.0f
        pitch = 0.0f
        canRotate = false
        lastBlockPos = null
    }

    override fun onDisable() {
        yaw = -180.0f
        pitch = 0.0f
        canRotate = false
        lastBlockPos = null
    }

    override fun setTargetPlace(targetPlace: PlaceInfo?) {
        if (lastBlockPos != targetPlace?.blockPos) {
            canRotate = false
            lastBlockPos = targetPlace?.blockPos
        }
        super.setTargetPlace(targetPlace)
    }

    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation? {
        val player = mc.thePlayer ?: return lockRotation
        val serverYaw = RotationUtils.serverRotation.yaw
        
        val currentYaw = adjustYaw(
            player.rotationYaw,
            getForwardValue().toFloat(),
            getLeftValue().toFloat()
        )
        
        val yawDiffTo180 = serverYaw + MathHelper.wrapAngleTo180_float((currentYaw - 180.0f) - serverYaw)
        
        val diagonalYaw = if (isDiagonal(currentYaw)) {
            yawDiffTo180
        } else {
            val mod = (currentYaw + 180.0f) % 90.0f
            val offsetSign = if (mod < 45.0f) 1.0f else -1.0f
            serverYaw + MathHelper.wrapAngleTo180_float((currentYaw - 135.0f * offsetSign) - serverYaw)
        }
        
        if (!canRotate) {
            yaw = quantize(diagonalYaw)
            if (pitch == 0.0f) pitch = quantize(85.0f)
        }
        
        currentTargetPlace?.let { targetPlace ->
            findBestHitVec(targetPlace.blockPos, targetPlace.enumFacing, serverYaw)?.let {
                yaw = it.yaw
                pitch = it.pitch
                canRotate = true
            }
        }
        
        return Rotation(yaw, pitch)
    }

    private fun adjustYaw(baseYaw: Float, forward: Float, strafe: Float): Float {
        var result = baseYaw
        if (forward < 0.0f) result += 180.0f
        if (strafe != 0.0f) {
            val multiplier = if (forward == 0.0f) 1.0f else 0.5f * sign(forward)
            result += -90.0f * multiplier * sign(strafe)
        }
        return MathHelper.wrapAngleTo180_float(result)
    }

    private fun getForwardValue(): Int {
        var value = 0
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindForward)) value++
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindBack)) value--
        return value
    }

    private fun getLeftValue(): Int {
        var value = 0
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) value++
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) value--
        return value
    }

    private fun isDiagonal(yaw: Float): Boolean {
        val absYaw = abs(yaw % 90.0f)
        return absYaw > 20.0f && absYaw < 70.0f
    }

    private fun findBestHitVec(blockPos: BlockPos, facing: EnumFacing, serverYaw: Float): Rotation? {
        val player = mc.thePlayer ?: return null
        val eyeHeight = player.getEyeHeight()
        val reach = mc.playerController.blockReachDistance.toDouble()

        val xOffsets = if (facing == EnumFacing.EAST) doubleArrayOf(1.0) else if (facing == EnumFacing.WEST) doubleArrayOf(0.0) else PLACE_OFFSETS
        val yOffsets = if (facing == EnumFacing.UP) doubleArrayOf(1.0) else if (facing == EnumFacing.DOWN) doubleArrayOf(0.0) else PLACE_OFFSETS
        val zOffsets = if (facing == EnumFacing.SOUTH) doubleArrayOf(1.0) else if (facing == EnumFacing.NORTH) doubleArrayOf(0.0) else PLACE_OFFSETS

        var bestYaw = -180.0f
        var bestPitch = 0.0f
        var bestDiff = Float.MAX_VALUE
        val baseYaw = serverYaw + MathHelper.wrapAngleTo180_float(yaw - serverYaw)

        for (dx in xOffsets) {
            for (dy in yOffsets) {
                for (dz in zOffsets) {
                    val relX = blockPos.x + dx - player.posX
                    val relY = blockPos.y + dy - player.posY - eyeHeight
                    val relZ = blockPos.z + dz - player.posZ

                    val rotations = getRotationsTo(relX, relY, relZ, baseYaw, pitch)
                    val mop = player.rayTraceWithCustomRotation(reach, rotations[0], rotations[1])
                    
                    if (mop != null && 
                        mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                        mop.blockPos == blockPos && mop.sideHit == facing) {
                        
                        val totalDiff = abs(rotations[0] - baseYaw) + abs(rotations[1] - pitch)
                        if ((bestYaw == -180.0f && bestPitch == 0.0f) || totalDiff < bestDiff) {
                            bestYaw = rotations[0]
                            bestPitch = rotations[1]
                            bestDiff = totalDiff
                        }
                    }
                }
            }
        }

        return if (bestYaw != -180.0f || bestPitch != 0.0f) Rotation(bestYaw, bestPitch) else null
    }

    private fun getRotationsTo(targetX: Double, targetY: Double, targetZ: Double, currentYaw: Float, currentPitch: Float): FloatArray {
        val horizontalDistance = sqrt(targetX * targetX + targetZ * targetZ)
        val yawDelta = MathHelper.wrapAngleTo180_float(
            (atan2(targetZ, targetX) * 180.0 / Math.PI).toFloat() - 90.0f - currentYaw
        )
        val pitchDelta = MathHelper.wrapAngleTo180_float(
            (-atan2(targetY, horizontalDistance) * 180.0 / Math.PI).toFloat() - currentPitch
        )
        return floatArrayOf(quantize(currentYaw + yawDelta), quantize(currentPitch + pitchDelta))
    }

    private fun quantize(angle: Float) = angle - angle % 0.0096f
}