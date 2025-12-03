package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION_LOW
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft


class LowHopTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        val player = mc.thePlayer ?: return false
        
        MovementUtils.strafe()
        
        if (player.onGround) {
            player.motionY = JUMP_MOTION_LOW
            return true
        }
        
        return false
    }
}
