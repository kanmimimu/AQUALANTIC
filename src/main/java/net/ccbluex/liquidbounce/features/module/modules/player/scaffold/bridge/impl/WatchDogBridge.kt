package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION_ALT
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockAir
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos


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

            if (player.onGround) {
                if (watchdogBoostValue()) {

                    if (!Speed.state) {
                        MovementUtils.setMotion(getSpeed().toDouble())
                        player.motionY = JUMP_MOTION_ALT
                        return true
                    }
                } else {

                    MovementUtils.jump(true)
                    return true
                }
            }
            

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

        return true
    }
    
    override fun shouldKeepY(): Boolean {
        return false
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        val player = mc.thePlayer ?: return null
        if (lastGroundY == null) return null
        

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
