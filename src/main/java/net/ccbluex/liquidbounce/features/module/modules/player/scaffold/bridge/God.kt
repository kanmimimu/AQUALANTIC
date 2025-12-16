package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.minecraft.util.BlockPos

class God(
    private val motionMode: () -> String,
    private val minBlockPlace: () -> Int,
    private val maxBlockPlace: () -> Int
) : Bridge("GodBridge") {

    private var godBridgePlaceTicks = 0
    private var randomGodBridgePlaceTicks = 8

    override fun onTick() {
        if (godBridgePlaceTicks > randomGodBridgePlaceTicks && mc.thePlayer.onGround) {
            if (!motionMode().equals("MovementInput", true)) {
                MovementUtils.jump(true, motionMode().equals("Motion", true))
                godBridgePlaceTicks = 0
                randomGodBridgePlaceTicks = RandomUtils.nextInt(minBlockPlace(), maxBlockPlace())
            }
        }
    }

    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        return BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
    }

    fun incrementPlaceTicks() {
        godBridgePlaceTicks++
    }

    fun resetPlaceTicks() {
        godBridgePlaceTicks = 0
    }

    override fun onEnable() {
        godBridgePlaceTicks = 0
        randomGodBridgePlaceTicks = 8
    }
}
