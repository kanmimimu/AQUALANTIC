package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.minecraft.block.BlockAir
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.BlockPos

class WatchDog(
    private val watchdogTelly: () -> Boolean,
    private val watchDogDelay: () -> Int,
    private val watchdogBoost: () -> Boolean,
    private val watchdogExtraClick: () -> Boolean,
    private val lowHopMode: () -> String,
    private val getSpeed: () -> Float
) : Bridge("WatchDog") {

    override fun onTick() {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                if (watchdogBoost()) {
                    if (!Speed.state) {
                        MovementUtils.setMotion(getSpeed().toDouble())
                        mc.thePlayer.motionY = 0.4191
                    }
                } else {
                    MovementUtils.jump(true)
                }
            }
            if (lowHopMode().equals("WatchDog", true)) {
                if (PlayerUtils.offGroundTicks == 5) {
                    mc.thePlayer.motionY = MovementUtils.predictedMotion(mc.thePlayer.motionY, 2)
                }
            }
        }
    }

    override fun getBlockPosition(lastGroundY: Int): BlockPos {
        if (!watchdogExtraClick() || watchdogBoost() && GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)) {
            return BlockPos(mc.thePlayer.posX, lastGroundY.toDouble() - 1.0, mc.thePlayer.posZ)
        }

        val blockAtGround = BlockUtils.getBlock(
            BlockPos(mc.thePlayer.posX, lastGroundY.toDouble() - 1.0, mc.thePlayer.posZ)
        )
        val blockAboveGround = BlockUtils.getBlock(
            BlockPos(mc.thePlayer.posX, lastGroundY.toDouble(), mc.thePlayer.posZ)
        )

        val shouldPlaceAbove = blockAtGround !is BlockAir &&
                blockAboveGround is BlockAir &&
                mc.thePlayer.posY > lastGroundY &&
                mc.thePlayer.posY + MovementUtils.predictedMotion(mc.thePlayer.motionY, 3) < lastGroundY + 1 &&
                PlayerUtils.offGroundTicks > 1

        return if (shouldPlaceAbove) {
            BlockPos(mc.thePlayer.posX, lastGroundY.toDouble(), mc.thePlayer.posZ)
        } else {
            BlockPos(mc.thePlayer.posX, lastGroundY.toDouble() - 1.0, mc.thePlayer.posZ)
        }
    }

    override fun shouldPlace(): Boolean {
        if (watchdogTelly()) {
            return PlayerUtils.offGroundTicks >= watchDogDelay()
        }
        return true
    }
}
