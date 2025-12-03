package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge.BridgeMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockAir
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos


class AndromedaBridge(
    private val andJump: () -> Boolean,
    private val onLockRotationNull: () -> Unit
) : BridgeMode {
    
    override fun isActive(mc: Minecraft): Boolean {
        return true
    }
    
    override fun execute(mc: Minecraft, towerStatus: Boolean): Boolean {
        val player = mc.thePlayer ?: return false
        
        val posBelow = BlockPos(player.posX, player.posY, player.posZ).down()
        val posAbove = BlockPos(player.posX, player.posY + 2, player.posZ)
        
        val blockBelow = BlockUtils.getBlock(posBelow)
        val blockAbove = BlockUtils.getBlock(posAbove)
        

        if (blockBelow !is BlockAir && blockAbove !is BlockAir) {

            if (andJump() && player.onGround) {
                MovementUtils.jump(true)
            }
            

            onLockRotationNull()
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
        
        val posBelow = BlockPos(player.posX, player.posY, player.posZ).down()
        val blockBelow = BlockUtils.getBlock(posBelow)
        

        return if (blockBelow is BlockAir) {
            player.posY - 1.0
        } else {

            player.posY + 2.0
        }
    }
}
