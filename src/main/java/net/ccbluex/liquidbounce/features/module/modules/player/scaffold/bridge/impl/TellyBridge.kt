package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft


class TellyBridge : BridgeMode {
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        if (towerStatus) return false
        
        val player = mc.thePlayer ?: return false
        

        if (player.onGround && MovementUtils.isMoving()) {
            MovementUtils.jump(true)
            return true
        }
        
        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {

        return true
    }
    
    override fun shouldKeepY(): Boolean {
        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        val player = mc.thePlayer ?: return null
        if (lastGroundY == null) return null
        

        return lastGroundY.toDouble() - 1.0
    }
}
