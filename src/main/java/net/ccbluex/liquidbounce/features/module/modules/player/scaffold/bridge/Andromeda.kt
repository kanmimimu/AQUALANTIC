package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockAir
import net.minecraft.util.BlockPos

class Andromeda(private val andJump: () -> Boolean) : Bridge("Andromeda") {
    var lockRotation: Any? = null

    override fun onTick() {
        val blockBelow = BlockUtils.getBlock(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
        )
        val blockAbove = BlockUtils.getBlock(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ)
        )
        if (blockBelow !is BlockAir && blockAbove !is BlockAir) {
            if (andJump() && mc.thePlayer.onGround) {
                MovementUtils.jump(true)
            }
            lockRotation = null
        }
    }

    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        val blockBelow = BlockUtils.getBlock(
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
        )
        return if (blockBelow is BlockAir) {
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
        } else {
            BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ)
        }
    }
}
