package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.watchdog

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.potion.Potion

class WatchDog8Tick : SpeedMode("WatchDog8Tick") {
    override fun onUpdate() {
        if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.motionY = 0.41999998688698
            MovementUtils.setMotion(getSpeed().toDouble())
            return
        }

        if (PlayerUtils.offGroundTicks < 9) {
            when (PlayerUtils.offGroundTicks) {
                1 -> mc.thePlayer.motionY = 0.38999998569488
                2 -> mc.thePlayer.motionY = 0.30379999189377
                3 -> mc.thePlayer.motionY = 0.08842400075912
                4 -> mc.thePlayer.motionY = -0.19174457909538
                5 -> mc.thePlayer.motionY = -0.26630949469659
                6 -> mc.thePlayer.motionY = -0.26438340940798
                7 -> mc.thePlayer.motionY = -0.33749574778843
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