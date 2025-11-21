package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.watchdog

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.potion.Potion

class WatchDog9Tick : SpeedMode("WatchDog9Tick") {
    override fun onUpdate() {
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.motionY = 0.42
            MovementUtils.setMotion(getSpeed().toDouble())
            return
        }

        if (PlayerUtils.offGroundTicks < 9) {
            val simpleY = (mc.thePlayer.posY % 1 * 10000).toInt()
            when (simpleY) {
                13 -> mc.thePlayer.motionY -= 0.02483
                2000 -> mc.thePlayer.motionY -= 0.1913
                7016 -> mc.thePlayer.motionY += 0.08
            }
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