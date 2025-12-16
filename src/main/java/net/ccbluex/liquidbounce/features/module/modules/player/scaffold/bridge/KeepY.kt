package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.minecraft.util.BlockPos

class KeepY(private val keepYModeValue: () -> String) : Bridge("Keep-Y") {
    private var keepYTicks = 0
    private var firstKeepYPlace = false

    override fun onTick() {
        if (MovementUtils.isMoving() && onGround()) {
            when (keepYModeValue().lowercase()) {
                "onlyspeed" -> if (Speed.state) MovementUtils.jump(true)
                "vanilla", "jump a", "jump b" -> MovementUtils.jump(true)
            }
        }
        if (mc.thePlayer.onGround) {
            keepYTicks = 0
            firstKeepYPlace = false
        } else {
            keepYTicks++
        }
    }

    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        val yPos = if (isSpecialTick()) {
            lastGroundY.toDouble()
        } else {
            lastGroundY.toDouble() - 1.0
        }
        return BlockPos(mc.thePlayer.posX, yPos, mc.thePlayer.posZ)
    }

    fun isSpecialTick(): Boolean {
        if (mc.thePlayer.onGround) return false
        return when (keepYModeValue().lowercase()) {
            "jump a" -> !firstKeepYPlace && keepYTicks == 3
            "jump b" -> !firstKeepYPlace && keepYTicks == 8
            else -> false
        }
    }

    fun markFirstPlace() {
        firstKeepYPlace = true
    }

    private fun onGround(): Boolean {
        return mc.thePlayer.onGround || PlayerUtils.offGroundTicks == 0
    }

    override fun onEnable() {
        keepYTicks = 0
        firstKeepYPlace = false
    }
}
