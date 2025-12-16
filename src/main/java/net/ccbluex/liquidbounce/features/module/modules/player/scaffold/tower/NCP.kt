package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import kotlin.math.floor

class NCP : Tower("NCP") {
    override fun onMove() {
        MovementUtils.strafe()
        if (mc.thePlayer.posY % 1 <= 0.00153598) {
            mc.thePlayer.setPosition(
                mc.thePlayer.posX,
                floor(mc.thePlayer.posY),
                mc.thePlayer.posZ
            )
            mc.thePlayer.motionY = 0.42
        } else if (mc.thePlayer.posY % 1 < 0.1 && PlayerUtils.offGroundTicks != 0) {
            mc.thePlayer.setPosition(
                mc.thePlayer.posX,
                floor(mc.thePlayer.posY),
                mc.thePlayer.posZ
            )
        }
    }
}
