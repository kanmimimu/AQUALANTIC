package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.NCP_LOW_POSITION_THRESHOLD
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.NCP_POSITION_THRESHOLD
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.client.Minecraft
import kotlin.math.floor


class NCPTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        val player = mc.thePlayer ?: return false
        
        MovementUtils.strafe()
        
        if (player.posY % 1 <= NCP_POSITION_THRESHOLD) {
            player.setPosition(player.posX, floor(player.posY), player.posZ)
            player.motionY = JUMP_MOTION
            return true
        } else if (player.posY % 1 < NCP_LOW_POSITION_THRESHOLD && PlayerUtils.offGroundTicks != 0) {
            player.setPosition(player.posX, floor(player.posY), player.posZ)
            return true
        }
        
        return false
    }
}
