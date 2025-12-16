package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.potion.Potion

class FastJump : Tower("FastJump") {
    override fun onMove() {
        MovementUtils.strafe()
        if (mc.thePlayer.motionY < 0) {
            mc.thePlayer.motionY = 0.42
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                mc.thePlayer.motionY += ((mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1) * 0.1f).toDouble()
            }
        }
    }
}
