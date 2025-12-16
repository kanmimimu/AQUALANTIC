package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.minecraft.util.BlockPos

class Normal : Bridge("Normal") {
    override fun onTick() {}
    
    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        return BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
    }
}
