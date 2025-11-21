package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.intave

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class Intave1 : SpeedMode("Intave1") {
    override fun onUpdate() {
        mc.timer.timerSpeed = 1.00f
        if (!MovementUtils.isMoving()) {
            return
        }
        if (mc.thePlayer.onGround) {
            MovementUtils.jump(false)
            mc.timer.timerSpeed = 1.00f
        }
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
            mc.thePlayer.speedInAir = 0.02F
            mc.timer.timerSpeed = 1.8F
        }
    }

    override fun onDisable() {
        mc.thePlayer!!.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
    }
}