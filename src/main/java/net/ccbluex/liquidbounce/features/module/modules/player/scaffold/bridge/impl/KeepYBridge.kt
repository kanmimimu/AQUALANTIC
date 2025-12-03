package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft

/**
 * Keep-Yブリッジモード
 * 
 * Y座標を保持しながらブリッジング
 * 複数のサブモード(Vanilla、OnlySpeed、Jump A、Jump B)をサポート
 */
class KeepYBridge(
    private val keepYMode: () -> String,
    private val isKeepYSpecialTick: () -> Boolean
) : BridgeMode {
    
    private var keepYTicks = 0
    private var firstKeepYPlace = false
    
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        if (towerStatus) return false
        
        val player = mc.thePlayer ?: return false
        
        // 移動中で地面にいる場合
        if (MovementUtils.isMoving() && player.onGround) {
            when (keepYMode().lowercase()) {
                "onlyspeed" -> {
                    // OnlySpeed: Speedモジュールがアクティブな場合のみジャンプ
                    if (Speed.state) {
                        MovementUtils.jump(true)
                        return true
                    }
                }
                "vanilla", "jump a", "jump b" -> {
                    // その他のモード: 常にジャンプ
                    MovementUtils.jump(true)
                    return true
                }
            }
        }
        
        // ティック数管理
        if (player.onGround) {
            keepYTicks = 0
            firstKeepYPlace = false
        } else {
            keepYTicks++
        }
        
        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {
        return true
    }
    
    override fun shouldKeepY(): Boolean {
        // Keep-YモードはcanKeepYをtrueに設定
        return true
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        if (lastGroundY == null) return null
        
        // 特別なティックの場合はlastGroundYの高さに配置
        val yPos = if (isKeepYSpecialTick()) {
            lastGroundY.toDouble()
        } else {
            lastGroundY.toDouble() - 1.0
        }
        
        return yPos
    }
    
    fun getKeepYTicks(): Int = keepYTicks
    
    fun setFirstKeepYPlace(value: Boolean) {
        firstKeepYPlace = value
    }
    
    fun reset() {
        keepYTicks = 0
        firstKeepYPlace = false
    }
}
