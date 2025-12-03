package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft

/**
 * KeepUPブリッジモード
 * 
 * 移動中に常にジャンプし続ける
 */
class KeepUPBridge : BridgeMode {
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        if (towerStatus) return false
        
        val player = mc.thePlayer ?: return false
        
        // 移動中で地面にいる場合、ジャンプ
        if (MovementUtils.isMoving() && player.onGround) {
            MovementUtils.jump(true)
            return true
        }
        
        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {
        return true
    }
    
    override fun shouldKeepY(): Boolean {
        // KeepUPモードはcanKeepYをfalseに設定
        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        return null
    }
}
