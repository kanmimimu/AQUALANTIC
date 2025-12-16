package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.util.BlockPos

class KeepUP : Bridge("KeepUP") {
    override fun onTick() {
        if (MovementUtils.isMoving() && onGround()) {
            MovementUtils.jump(true)
        }
    }

    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        return BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
    }

    private fun onGround(): Boolean {
        return mc.thePlayer.onGround || PlayerUtils.offGroundTicks == 0
    }
}
