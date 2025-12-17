package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldMisc
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.minecraft.util.MathHelper
import kotlin.math.sign

class WatchDog2 : ScaffoldRotation("WatchDog2") {
    private var yaw = 0f
    private var yawAngle = 126.425f
    private var minPitch = 76f
    private var minOffset = 11
    private var firstStroke = 0L
    private var set2 = false
    private var was451 = false
    private var was452 = false
    private var switchvl = 0
    private var theYaw = 0f
    private var yawOffset = 5f
    private var dynamic = 0

    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation {
        val moveAngle = ScaffoldMisc.getMovementAngle().toFloat()
        val relativeYaw = mc.thePlayer.rotationYaw + moveAngle
        val normalizedYaw = (relativeYaw % 360 + 360) % 360
        val quad = normalizedYaw % 90

        val side = MathHelper.wrapAngleTo180_float(ScaffoldMisc.getMotionYaw() - yaw)
        val yawBackwards = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - ScaffoldMisc.hardcodedYaw()
        val blockYawOffset = if (lockRotation != null) {
            MathHelper.wrapAngleTo180_float(yawBackwards - lockRotation.yaw)
        } else 5f

        val strokeDelay = 250L
        val first = 76f
        val sec = 78f

        when {
            quad <= 5 || quad >= 85 -> {
                yawAngle = 126.425f; minOffset = 11; minPitch = first
            }

            quad > 5 && quad <= 15 || quad >= 75 && quad < 85 -> {
                yawAngle = 127.825f; minOffset = 9; minPitch = first
            }

            quad > 15 && quad <= 25 || quad >= 65 && quad < 75 -> {
                yawAngle = 129.625f; minOffset = 8; minPitch = first
            }

            quad > 25 && quad <= 32 || quad >= 58 && quad < 65 -> {
                yawAngle = 130.485f; minOffset = 7; minPitch = sec
            }

            quad > 32 && quad <= 38 || quad >= 52 && quad < 58 -> {
                yawAngle = 133.485f; minOffset = 6; minPitch = sec
            }

            quad > 38 && quad <= 42 || quad >= 48 && quad < 52 -> {
                yawAngle = 135.625f; minOffset = 4; minPitch = sec
            }

            quad > 42 && quad <= 45 || quad >= 45 && quad < 48 -> {
                yawAngle = 137.625f; minOffset = 3; minPitch = sec
            }
        }

        val offset = yawAngle

        if (switchvl > 0) {
            firstStroke = System.currentTimeMillis()
            switchvl = 0
        }
        if (firstStroke > 0 && (System.currentTimeMillis() - firstStroke) > strokeDelay) {
            firstStroke = 0
        }

        val usePitch = if (lockRotation != null && lockRotation.pitch >= minPitch) {
            lockRotation.pitch
        } else {
            minPitch
        }

        dynamic = if (lockRotation != null) 1 else 2
        yawOffset = blockYawOffset

        if (!MovementUtils.isMoving() || MovementUtils.getSpeed() <= 0.001) {
            return Rotation(theYaw, usePitch)
        }

        val motionYaw = ScaffoldMisc.getMotionYaw()
        val newYaw = motionYaw - offset * sign(MathHelper.wrapAngleTo180_float(motionYaw - yaw))
        yaw = MathHelper.wrapAngleTo180_float(newYaw)

        if (quad > 3 && quad < 87 && dynamic > 0) {
            if (quad < 45f) {
                if (firstStroke == 0L) {
                    set2 = side < 0
                }
                if (was452) switchvl++
                was451 = true
                was452 = false
            } else {
                if (firstStroke == 0L) {
                    set2 = side >= 0
                }
                if (was451) switchvl++
                was452 = true
                was451 = false
            }
        }

        val minSwitch = if (!ScaffoldMisc.isDiagonal(mc.thePlayer.rotationYaw)) 9f else 15f

        if (side >= 0) {
            if (yawOffset <= -minSwitch && firstStroke == 0L && dynamic > 0) {
                if (quad <= 3 || quad >= 87) {
                    if (set2) switchvl++
                    set2 = false
                }
            } else if (yawOffset >= 0 && firstStroke == 0L && dynamic > 0) {
                if (quad <= 3 || quad >= 87) {
                    if (yawOffset >= minSwitch) {
                        if (!set2) switchvl++
                        set2 = true
                    }
                }
            }
            if (set2) {
                var clampedOffset = yawOffset
                if (clampedOffset < 0) clampedOffset = 0f
                if (clampedOffset > minOffset) clampedOffset = minOffset.toFloat()
                theYaw = (yaw + offset * 2) - clampedOffset
                return Rotation(theYaw, usePitch)
            }
        } else {
            if (yawOffset >= minSwitch && firstStroke == 0L && dynamic > 0) {
                if (quad <= 3 || quad >= 87) {
                    if (set2) switchvl++
                    set2 = false
                }
            } else if (yawOffset <= 0 && firstStroke == 0L && dynamic > 0) {
                if (quad <= 3 || quad >= 87) {
                    if (yawOffset <= -minSwitch) {
                        if (!set2) switchvl++
                        set2 = true
                    }
                }
            }
            if (set2) {
                var clampedOffset = yawOffset
                if (clampedOffset > 0) clampedOffset = 0f
                if (clampedOffset < -minOffset) clampedOffset = -minOffset.toFloat()
                theYaw = (yaw - offset * 2) - clampedOffset
                return Rotation(theYaw, usePitch)
            }
        }

        var clampedOffset = yawOffset
        if (side >= 0) {
            if (clampedOffset > 0) clampedOffset = 0f
            if (clampedOffset < -minOffset) clampedOffset = -minOffset.toFloat()
        } else {
            if (clampedOffset < 0) clampedOffset = 0f
            if (clampedOffset > minOffset) clampedOffset = minOffset.toFloat()
        }
        theYaw = yaw - clampedOffset
        return Rotation(theYaw, usePitch)
    }

    override fun onEnable() {
        yaw = 0f
        yawAngle = 126.425f
        minPitch = 76f
        minOffset = 11
        firstStroke = 0L
        set2 = false
        was451 = false
        was452 = false
        switchvl = 0
        theYaw = 0f
        yawOffset = 5f
        dynamic = 0
    }
}
