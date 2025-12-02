package net.ccbluex.liquidbounce.features.module.modules.player.scaffold

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.AAC_POSITION_THRESHOLD
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.BLOCKSMC_MOTION_DOWN
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION_AAC
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.JUMP_MOTION_LOW
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.NCP_LOW_POSITION_THRESHOLD
import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.ScaffoldConstants.NCP_POSITION_THRESHOLD
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils.isReplaceable
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.minecraft.client.Minecraft
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import kotlin.math.floor

object ScaffoldTowerHelper {
    private var towerTick = 0
    
    fun resetTowerTick() {
        towerTick = 0
    }

    fun doTowerMove(mode: String, mc: Minecraft) {
        val player = mc.thePlayer ?: return
        
        when (mode.lowercase()) {
            "ncp" -> {
                MovementUtils.strafe()
                if (player.posY % 1 <= NCP_POSITION_THRESHOLD) {
                    player.setPosition(player.posX, floor(player.posY), player.posZ)
                    player.motionY = JUMP_MOTION
                } else if (player.posY % 1 < NCP_LOW_POSITION_THRESHOLD && PlayerUtils.offGroundTicks != 0) {
                    player.setPosition(player.posX, floor(player.posY), player.posZ)
                }
            }
            
            "blocksmc" -> {
                MovementUtils.strafe()
                if (player.onGround) {
                    player.motionY = JUMP_MOTION
                }
            }
            
            "vanilla" -> {
                player.motionY = JUMP_MOTION
            }
            
            "lowhop" -> {
                MovementUtils.strafe()
                if (player.onGround) {
                    player.motionY = JUMP_MOTION_LOW
                }
            }
            
            "fastjump" -> {
                MovementUtils.strafe()
                if (player.motionY < 0) {
                    player.motionY = JUMP_MOTION
                    if (player.isPotionActive(Potion.jump)) {
                        player.motionY += ((player.getActivePotionEffect(Potion.jump).amplifier + 1) * 0.1f).toDouble()
                    }
                }
            }
            
            "aac" -> {
                MovementUtils.strafe()
                if (player.posY % 1 <= AAC_POSITION_THRESHOLD) {
                    player.setPosition(player.posX, floor(player.posY), player.posZ)
                    player.motionY = JUMP_MOTION_AAC
                }
            }
            
            "extra" -> {
                val yState = (player.posY % 1.0 * 100.0).toInt()
                when (towerTick) {
                    0 -> {
                        if (player.onGround) {
                            towerTick = 1
                            player.motionY = BLOCKSMC_MOTION_DOWN
                        }
                    }
                    1 -> {
                        if (yState == 0 && isReplaceable(BlockPos(player.posX, player.posY - 1.0, player.posZ))) {
                            towerTick = 2
                            player.motionY = JUMP_MOTION
                        } else {
                            towerTick = 0
                        }
                    }
                    2 -> {
                        towerTick = 3
                        player.motionY -= RandomUtils.nextDouble(0.00101, 0.00109)
                    }
                    3 -> {
                        towerTick = 1
                        player.motionY = 1.0 - player.posY % 1.0
                    }
                    else -> {
                        towerTick = 0
                    }
                }
            }
        }
    }
}
