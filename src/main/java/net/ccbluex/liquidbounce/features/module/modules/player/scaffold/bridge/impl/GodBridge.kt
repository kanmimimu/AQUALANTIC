package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.minecraft.client.Minecraft


class GodBridge(
    private val motionValue: () -> String,
    private val minBlockPlace: () -> Int,
    private val maxBlockPlace: () -> Int,
    private val onPlaced: () -> Unit,
    private val resetPlaceTicks: () -> Unit
) : BridgeMode {
    
    private var godBridgePlaceTicks = 0
    private var randomGodBridgePlaceTicks = 8
    
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        val player = mc.thePlayer ?: return false
        

        if (godBridgePlaceTicks > randomGodBridgePlaceTicks && !towerStatus && player.onGround) {
            val motion = motionValue()
            if (motion != "MovementInput") {

                MovementUtils.jump(true, motion == "Motion")
                godBridgePlaceTicks = 0
                randomGodBridgePlaceTicks = RandomUtils.nextInt(minBlockPlace(), maxBlockPlace())
                return true
            }
        }
        
        return false
    }
    
    fun incrementPlaceTicks() {
        godBridgePlaceTicks++
    }
    
    fun reset() {
        godBridgePlaceTicks = 0
        randomGodBridgePlaceTicks = 0
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
