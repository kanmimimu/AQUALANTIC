package net.ccbluex.liquidbounce.features.module.modules.player.scaffold

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.MathHelper
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.round
import kotlin.math.roundToInt

object ScaffoldMisc : MinecraftInstance() {

    val steps45 = arrayListOf(-135f, -45f, 45f, 135f)
    val steps4590 = arrayListOf(-180f, -135f, -45f, 45f, 135f, 180f)

    val isLookingDiagonally: Boolean
        get() {
            val player = mc.thePlayer ?: return false
            val yaw = round(abs(MathHelper.wrapAngleTo180_float(player.rotationYaw)).roundToInt() / 45f) * 45f
            return floatArrayOf(45f, 135f).any { yaw == it } &&
                    player.movementInput.moveForward != 0f &&
                    player.movementInput.moveStrafe == 0f
        }

    fun isDiagonal(yaw: Float): Boolean {
        val absYaw = abs(yaw % 90.0F)
        return absYaw > 20.0F && absYaw < 70.0F
    }

    fun hardcodedYaw(): Float {
        var simpleYaw = 0f
        val w = GameSettings.isKeyDown(mc.gameSettings.keyBindForward)
        val s = GameSettings.isKeyDown(mc.gameSettings.keyBindBack)
        val a = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
        val d = GameSettings.isKeyDown(mc.gameSettings.keyBindRight)
        val dupe = a && d
        if (w) {
            simpleYaw -= 180f
            if (!dupe) {
                if (a) simpleYaw += 45f
                if (d) simpleYaw -= 45f
            }
        } else if (!s) {
            simpleYaw -= 180f
            if (!dupe) {
                if (a) simpleYaw += 90f
                if (d) simpleYaw -= 90f
            }
        } else if (!w) {
            if (!dupe) {
                if (a) simpleYaw -= 45f
                if (d) simpleYaw += 45f
            }
        }
        return simpleYaw
    }

    fun getMotionYaw(): Float {
        return Math.toDegrees(atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)).toFloat() - 90f
    }

    fun getMovementAngle(): Double {
        val angle = Math.toDegrees(atan2(-mc.thePlayer.moveStrafing.toDouble(), mc.thePlayer.moveForward.toDouble()))
        return if (angle == -0.0) 0.0 else angle
    }
}
