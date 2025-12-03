package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft

/**
 * Tellyブリッジモード
 * 
 * ジャンプしながらブロックを配置する高速ブリッジング
 */
class TellyBridge : BridgeMode {
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        if (towerStatus) return false
        
        val player = mc.thePlayer ?: return false
        
        // 地面にいて移動中の場合、ジャンプ
        if (player.onGround && MovementUtils.isMoving()) {
            MovementUtils.jump(true)
            return true
        }
        
        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {
        // prevToweredがfalseの場合のみTellyティック数チェックを行う
        // （実際のチェックはScaffold.kt側で行われる）
        return true
    }
    
    override fun shouldKeepY(): Boolean {
        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        val player = mc.thePlayer ?: return null
        if (lastGroundY == null) return null
        
        // Tellyモードでは lastGroundY - 1.0 の位置に配置
        return lastGroundY.toDouble() - 1.0
    }
}
