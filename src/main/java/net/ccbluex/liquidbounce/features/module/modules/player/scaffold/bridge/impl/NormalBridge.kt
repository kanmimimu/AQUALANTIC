package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.minecraft.client.Minecraft

/**
 * デフォルトのブリッジモード
 * 
 * 特別な処理を行わない基本的なブリッジング動作
 */
class NormalBridge : BridgeMode {
    override fun isActive(mc: Minecraft): Boolean {
        // Normalモードは常にアクティブ（デフォルト）
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        // Normalモードは特別な処理を行わない
        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {
        // 常に配置を許可
        return true
    }
    
    override fun shouldKeepY(): Boolean {
        // Y座標を保持しない
        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        // 特別なY座標処理なし
        return null
    }
}
