package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.MovementUtils

class LowHop : Tower("LowHop") {
    override fun onMove() {
        MovementUtils.strafe()
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.4
        }
    }
}
