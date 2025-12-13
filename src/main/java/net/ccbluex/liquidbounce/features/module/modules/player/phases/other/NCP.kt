package net.ccbluex.liquidbounce.features.module.modules.player.phases.other

import net.ccbluex.liquidbounce.event.BlockBBEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.player.phases.PhaseMode
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.timer.tickTimer
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import kotlin.math.cos
import kotlin.math.sin

class NCPPhase : PhaseMode("NCP") {
    private val tickTimer = tickTimer()
    
    private val clipDistanceValue = FloatValue("${valuePrefix}ClipDistance", 0.0625f, 0.01f, 0.2f)
    private val tickDelayValue = IntegerValue("${valuePrefix}TickDelay", 2, 1, 10)
    
    private var clipping = false

    override fun onEnable() {
        tickTimer.reset()
        clipping = false
    }
    
    override fun onDisable() {
        clipping = false
    }

    override fun onUpdate(event: UpdateEvent) {
        val player = mc.thePlayer ?: return
        
        val isInsideBlock = BlockUtils.collideBlockIntersects(player.entityBoundingBox) { block: Block? -> 
            block !is BlockAir 
        }
        
        if (isInsideBlock) {
            player.noClip = true
            player.motionY = 0.0
            player.onGround = true
            clipping = true
        }
        
        tickTimer.update()
        
        if (!player.isCollidedHorizontally) return
        if (!tickTimer.hasTimePassed(tickDelayValue.get())) return
        if (!(!isInsideBlock || player.isSneaking)) return
        
        val yaw = Math.toRadians(player.rotationYaw.toDouble())
        val direction = -sin(yaw)
        val dirZ = cos(yaw)
        val clipDist = clipDistanceValue.get().toDouble()
        
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                player.posX,
                player.posY,
                player.posZ,
                true
            )
        )
        mc.netHandler.addToSendQueue(
            C03PacketPlayer.C04PacketPlayerPosition(
                player.posX + direction * clipDist,
                player.posY,
                player.posZ + dirZ * clipDist,
                false
            )
        )
        
        val oldX = player.posX
        val oldZ = player.posZ
        
        for (i in 1..10) {
            val offsetX = direction * i
            val offsetZ = dirZ * i
            val targetPos = BlockPos(oldX + offsetX, player.posY, oldZ + offsetZ)
            val targetPosUp = BlockPos(oldX + offsetX, player.posY + 1, oldZ + offsetZ)
            
            if (BlockUtils.getBlock(targetPos) is BlockAir && 
                BlockUtils.getBlock(targetPosUp) is BlockAir) {
                player.setPosition(oldX + offsetX, player.posY, oldZ + offsetZ)
                break
            }
        }
        
        tickTimer.reset()
    }
    
    override fun onMove(event: MoveEvent) {
        if (clipping) {
            val player = mc.thePlayer ?: return
            
            if (!player.isSneaking) {
                event.y = 0.0
            }
        }
    }
    
    override fun onBlockBB(event: BlockBBEvent) {
        if (!clipping) return
        
        val player = mc.thePlayer ?: return
        val bb = event.boundingBox ?: return
        
        if (bb.maxY > player.posY) {
            event.boundingBox = AxisAlignedBB(
                bb.minX, bb.minY, bb.minZ,
                bb.maxX, player.posY, bb.maxZ
            )
        }
    }
}