package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION_ALT
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockAir
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.BlockPos

/**
 * WatchDogブリッジモード
 * 
 * WatchDogサーバー用の最適化されたブリッジング動作
 * ブーストモードとローホップをサポート
 */
class WatchDogBridge(
    private val watchdogBoostValue: () -> Boolean,
    private val lowHopValue: () -> String,
    private val watchdogExtraClick: () -> Boolean,
    private val getSpeed: () -> Float
) : BridgeMode {
    
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        if (towerStatus) return false
        
        val player = mc.thePlayer ?: return false
        
        if (MovementUtils.isMoving()) {
            // 地面にいる場合
            if (player.onGround) {
                if (watchdogBoostValue()) {
                    // ブーストモード: Speedモジュールがオフの場合のみブースト
                    if (!Speed.state) {
                        MovementUtils.setMotion(getSpeed().toDouble())
                        player.motionY = JUMP_MOTION_ALT
                        return true
                    }
                } else {
                    // 通常モード: ジャンプ
                    MovementUtils.jump(true)
                    return true
                }
            }
            
            // ローホップ処理
            if (lowHopValue() == "WatchDog") {
                if (PlayerUtils.offGroundTicks == 5) {
                    player.motionY = MovementUtils.predictedMotion(player.motionY, 2)
                    return true
                }
            }
        }
        
        return false
    }
    
    override fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean {
        // WatchDogモード固有の配置ロジックはScaffold.kt側で処理される
        return true
    }
    
    override fun shouldKeepY(): Boolean {
        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        val player = mc.thePlayer ?: return null
        if (lastGroundY == null) return null
        
        // WatchDogExtraClickが有効な場合の特別な処理
        if (watchdogExtraClick()) {
            val posBelow = BlockPos(player.posX, lastGroundY.toDouble() - 1.0, player.posZ)
            val posCurrent = BlockPos(player.posX, lastGroundY.toDouble(), player.posZ)
            
            val blockBelow = BlockUtils.getBlock(posBelow)
            val blockCurrent = BlockUtils.getBlock(posCurrent)
            
            val shouldPlaceAtCurrentY = blockBelow !is BlockAir && 
                blockCurrent is BlockAir && 
                player.posY > lastGroundY && 
                player.posY + MovementUtils.predictedMotion(player.motionY, 3) < lastGroundY + 1 &&
                PlayerUtils.offGroundTicks > 1
            
            return if (shouldPlaceAtCurrentY) {
                lastGroundY.toDouble()
            } else {
                lastGroundY.toDouble() - 1.0
            }
        }
        
        return lastGroundY.toDouble() - 1.0
    }
}
