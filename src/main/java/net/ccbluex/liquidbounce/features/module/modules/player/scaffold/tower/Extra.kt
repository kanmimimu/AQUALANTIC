package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.ccbluex.liquidbounce.utils.block.BlockUtils.isReplaceable
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.minecraft.util.BlockPos

class Extra : Tower("Extra") {
    private var towerTick = 0

    override fun onMove() {
        val yState = (mc.thePlayer.posY % 1.0 * 100.0).toInt()
        when (towerTick) {
            0 -> {
                if (mc.thePlayer.onGround) {
                    towerTick = 1
                    mc.thePlayer.motionY = -0.0784000015258789
                }
            }

            1 -> {
                if (yState == 0 && isReplaceable(
                        BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)
                    )
                ) {
                    towerTick = 2
                    mc.thePlayer.motionY = 0.42
                } else {
                    towerTick = 0
                }
            }

            2 -> {
                towerTick = 3
                mc.thePlayer.motionY -= RandomUtils.nextDouble(0.00101, 0.00109)
            }

            3 -> {
                towerTick = 1
                mc.thePlayer.motionY = 1.0 - mc.thePlayer.posY % 1.0
            }

            else -> {
                towerTick = 0
            }
        }
    }

    override fun onEnable() {
        towerTick = 0
    }
}
