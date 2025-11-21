package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.watchdog

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.potion.Potion

class WatchDog7Tick : SpeedMode("WatchDog7Tick") {
    override fun onUpdate() {
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.motionY = 0.42
            MovementUtils.setMotion(getSpeed().toDouble())
            return
        }

        val simpleY = (mc.thePlayer.posY % 1 * 10000).toInt()
        when (simpleY) {
            4200 -> mc.thePlayer.motionY = 0.39
            1138 -> mc.thePlayer.motionY -= 0.13
            2031 -> mc.thePlayer.motionY -= 0.2
        }
    }

    fun getSpeed(): Float {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier == 0) {
                return 0.5F
            } else if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier == 1) {
                return 0.53F
            }
        }
        return 0.48F
    }
}