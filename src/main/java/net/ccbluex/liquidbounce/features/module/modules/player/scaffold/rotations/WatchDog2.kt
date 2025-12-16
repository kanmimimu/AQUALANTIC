package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldMisc
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.minecraft.util.MathHelper
import kotlin.math.sign

class WatchDog2 : ScaffoldRotation("WatchDog2") {
    private var offsetYaw = 0f
    private var offsetYawAngle = 126.425f
    private var offsetMinPitch = 76f
    private var offsetFirstStroke = 0L
    private var offsetSet2 = false
    private var offsetWas451 = false
    private var offsetWas452 = false
    private var offsetSwitchVl = 0
    private var offsetTheYaw = 0f

    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation {
        val moveAngle = ScaffoldMisc.getMovementAngle().toFloat()
        val relativeYaw = mc.thePlayer.rotationYaw + moveAngle
        val normalizedYaw = (relativeYaw % 360 + 360) % 360
        val quad = normalizedYaw % 90

        val side = MathHelper.wrapAngleTo180_float(ScaffoldMisc.getMotionYaw() - offsetYaw)
        val yawBackwards = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - ScaffoldMisc.hardcodedYaw()
        val blockYawOffset = if (lockRotation != null) {
            MathHelper.wrapAngleTo180_float(yawBackwards - lockRotation.yaw)
        } else 5f

        val strokeDelay = 250L
        val first = 76f
        val sec = 78f
        var minOffset = 11

        when {
            quad <= 5 || quad >= 85 -> {
                offsetYawAngle = 126.425f; minOffset = 11; offsetMinPitch = first
            }

            quad > 5 && quad <= 15 || quad >= 75 && quad < 85 -> {
                offsetYawAngle = 127.825f; minOffset = 9; offsetMinPitch = first
            }

            quad > 15 && quad <= 25 || quad >= 65 && quad < 75 -> {
                offsetYawAngle = 129.625f; minOffset = 8; offsetMinPitch = first
            }

            quad > 25 && quad <= 32 || quad >= 58 && quad < 65 -> {
                offsetYawAngle = 130.485f; minOffset = 7; offsetMinPitch = sec
            }

            quad > 32 && quad <= 38 || quad >= 52 && quad < 58 -> {
                offsetYawAngle = 133.485f; minOffset = 6; offsetMinPitch = sec
            }

            quad > 38 && quad <= 42 || quad >= 48 && quad < 52 -> {
                offsetYawAngle = 135.625f; minOffset = 4; offsetMinPitch = sec
            }

            quad > 42 && quad <= 45 || quad >= 45 && quad < 48 -> {
                offsetYawAngle = 137.625f; minOffset = 3; offsetMinPitch = sec
            }
        }

        val offset = offsetYawAngle

        if (offsetSwitchVl > 0) {
            offsetFirstStroke = System.currentTimeMillis()
            offsetSwitchVl = 0
        }
        if (offsetFirstStroke > 0 && (System.currentTimeMillis() - offsetFirstStroke) > strokeDelay) {
            offsetFirstStroke = 0
        }

        val usePitch =
            if (lockRotation != null && lockRotation.pitch >= offsetMinPitch) lockRotation.pitch else offsetMinPitch

        if (!MovementUtils.isMoving() || MovementUtils.getSpeed() <= 0.001) {
            return Rotation(offsetTheYaw, usePitch)
        }

        val motionYaw = ScaffoldMisc.getMotionYaw()
        val newYaw = motionYaw - offset * sign(MathHelper.wrapAngleTo180_float(motionYaw - offsetYaw))
        offsetYaw = MathHelper.wrapAngleTo180_float(newYaw)

        if (quad > 3 && quad < 87) {
            if (quad < 45f) {
                if (offsetFirstStroke == 0L) offsetSet2 = side < 0
                if (offsetWas452) offsetSwitchVl++
                offsetWas451 = true
                offsetWas452 = false
            } else {
                if (offsetFirstStroke == 0L) offsetSet2 = side >= 0
                if (offsetWas451) offsetSwitchVl++
                offsetWas452 = true
                offsetWas451 = false
            }
        }

        if (!ScaffoldMisc.isDiagonal(mc.thePlayer.rotationYaw)) 9 else 15
        val calcOffset = blockYawOffset.coerceIn(-minOffset.toFloat(), minOffset.toFloat())

        offsetTheYaw = if (offsetSet2) {
            (offsetYaw + offset * 2) - calcOffset
        } else {
            offsetYaw - calcOffset
        }
        return Rotation(offsetTheYaw, usePitch)
    }

    override fun onEnable() {
        offsetYaw = 0f
        offsetYawAngle = 126.425f
        offsetMinPitch = 76f
        offsetFirstStroke = 0L
        offsetSet2 = false
        offsetWas451 = false
        offsetWas452 = false
        offsetSwitchVl = 0
        offsetTheYaw = 0f
    }
}
