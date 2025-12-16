package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.MovementUtils

class BlocksMC : Tower("BlocksMC") {
    override fun onMove() {
        MovementUtils.strafe()
        if (mc.thePlayer.onGround) {
            mc.thePlayer.motionY = 0.42
        }
    }
}
