package net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.grim

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.VelocityMode
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S12PacketEntityVelocity

class ZenticVelocity : VelocityMode("Zentic") {

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        val player = mc.thePlayer ?: return

        if (packet is S12PacketEntityVelocity && packet.entityID == player.entityId) {
            event.cancelEvent()

            mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SNEAKING))
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SNEAKING))
        }
    }
}