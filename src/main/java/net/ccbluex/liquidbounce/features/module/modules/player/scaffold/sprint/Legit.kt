package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.util.MathHelper
import kotlin.math.abs

class Legit : Sprint("Legit") {
    override fun isActive(): Boolean {
        val yawDiff = abs(
            (MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) -
                    MathHelper.wrapAngleTo180_float(RotationUtils.serverRotation.yaw)).toDouble()
        )
        return yawDiff < 90
    }
}
