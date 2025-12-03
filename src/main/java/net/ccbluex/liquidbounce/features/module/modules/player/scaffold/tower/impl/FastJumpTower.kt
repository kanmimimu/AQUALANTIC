package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft
import net.minecraft.potion.Potion


class FastJumpTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        val player = mc.thePlayer ?: return false
        
        MovementUtils.strafe()
        
        if (player.motionY < 0) {
            player.motionY = JUMP_MOTION
            

            if (player.isPotionActive(Potion.jump)) {
                player.motionY += ((player.getActivePotionEffect(Potion.jump).amplifier + 1) * 0.1f).toDouble()
            }
            
            return true
        }
        
        return false
    }
}
