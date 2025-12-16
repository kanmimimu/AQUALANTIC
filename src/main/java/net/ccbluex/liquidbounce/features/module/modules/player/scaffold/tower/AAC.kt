package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.MovementUtils
import kotlin.math.floor

class AAC : Tower("AAC") {
    override fun onMove() {
        MovementUtils.strafe()
        if (mc.thePlayer.posY % 1 <= 0.005) {
            mc.thePlayer.setPosition(
                mc.thePlayer.posX,
                floor(mc.thePlayer.posY),
                mc.thePlayer.posZ
            )
            mc.thePlayer.motionY = 0.41998
        }
    }
}
