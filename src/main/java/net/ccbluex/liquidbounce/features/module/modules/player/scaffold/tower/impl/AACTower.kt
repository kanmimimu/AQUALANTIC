package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.AAC_POSITION_THRESHOLD
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION_AAC
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft
import kotlin.math.floor


class AACTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        val player = mc.thePlayer ?: return false
        
        MovementUtils.strafe()
        
        if (player.posY % 1 <= AAC_POSITION_THRESHOLD) {
            player.setPosition(player.posX, floor(player.posY), player.posZ)
            player.motionY = JUMP_MOTION_AAC
            return true
        }
        
        return false
    }
}
