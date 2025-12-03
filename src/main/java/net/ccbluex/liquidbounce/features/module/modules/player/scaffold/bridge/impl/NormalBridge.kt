package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.minecraft.client.Minecraft


class NormalBridge : BridgeMode {
    override fun isActive(mc: Minecraft): Boolean {

        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {

        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {

        return true
    }
    
    override fun shouldKeepY(): Boolean {

        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {

        return null
    }
}
