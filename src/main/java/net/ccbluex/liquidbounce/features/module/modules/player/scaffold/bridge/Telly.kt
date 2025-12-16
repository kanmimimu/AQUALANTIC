package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.util.BlockPos

class Telly(private val tellyTicks: () -> Int) : Bridge("Telly") {
    override fun onTick() {
        if (onGround() && MovementUtils.isMoving()) {
            MovementUtils.jump(true)
        }
    }

    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        return BlockPos(mc.thePlayer.posX, lastGroundY.toDouble() - 1.0, mc.thePlayer.posZ)
    }

    override fun shouldPlace(): Boolean {
        return PlayerUtils.offGroundTicks >= tellyTicks() && PlayerUtils.offGroundTicks < 11
    }

    private fun onGround(): Boolean {
        return mc.thePlayer.onGround || PlayerUtils.offGroundTicks == 0
    }
}
