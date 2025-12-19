package net.ccbluex.liquidbounce.features.module.modules.movement.noslows.impl

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.noslows.NoSlowMode
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos

class NCPNoSlow : NoSlowMode("NCP") {
    private var tickCycle = 0

    override fun onPreMotion(event: MotionEvent) {
        if ((holdConsume || holdBow) && mc.thePlayer.isUsingItem) {
            tickCycle++
            if (tickCycle == 1) {
                PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9))
                PacketUtils.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                PacketUtils.sendPacketNoEvent(
                    C08PacketPlayerBlockPlacement(
                        BlockPos(-1, -1, -1),
                        0,
                        mc.thePlayer.heldItem,
                        0f,
                        0f,
                        0f
                    )
                )
            }
        } else {
            tickCycle = 0
        }
    }

    override fun onPostMotion(event: MotionEvent) {
        if (holdSword && mc.thePlayer.isUsingItem) {
            PacketUtils.sendPacketNoEvent(
                C08PacketPlayerBlockPlacement(
                    BlockPos.ORIGIN,
                    255,
                    mc.thePlayer.heldItem,
                    0f,
                    0f,
                    0f
                )
            )
        }
    }

    override fun slow(): Float {
        return 1F
    }
}