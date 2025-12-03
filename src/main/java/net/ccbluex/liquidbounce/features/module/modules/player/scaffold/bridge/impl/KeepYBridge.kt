package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.client.Minecraft


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
        

        if (MovementUtils.isMoving() && player.onGround) {
            when (keepYMode().lowercase()) {
                "onlyspeed" -> {

                    if (Speed.state) {
                        MovementUtils.jump(true)
                        return true
                    }
                }
                "vanilla", "jump a", "jump b" -> {

                    MovementUtils.jump(true)
                    return true
                }
            }
        }
        

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

        return true
    }
    
    override fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double? {
        if (lastGroundY == null) return null
        

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
