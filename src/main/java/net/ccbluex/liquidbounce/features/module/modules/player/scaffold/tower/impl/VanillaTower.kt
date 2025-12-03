package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.minecraft.client.Minecraft

/**
 * Vanilla タワーモード
 * バニラMinecraftのタワー動作
 */
class VanillaTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        val player = mc.thePlayer ?: return false
        
        player.motionY = JUMP_MOTION
        return true
    }
}
