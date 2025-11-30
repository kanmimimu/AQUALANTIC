package net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.grim

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.VelocityMode
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacketNoEvent
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0BPacketEntityAction.Action.*
import net.minecraft.network.play.server.S12PacketEntityVelocity

class GrimVelo : VelocityMode("Grim2") {
    
    private var hasReceivedVelocity = false

    override fun onDisable() {
        hasReceivedVelocity = false
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val player = mc.thePlayer ?: return

        // S12PacketEntityVelocity を受信した時の処理
        if (packet is S12PacketEntityVelocity && packet.entityID == player.entityId) {
            hasReceivedVelocity = true
            event.cancelEvent()

            // START_SNEAKING と STOP_SNEAKING パケットを送信
            sendPacketNoEvent(C0BPacketEntityAction(player, START_SNEAKING))
            sendPacketNoEvent(C0BPacketEntityAction(player, STOP_SNEAKING))
        }

        // hasReceivedVelocity が true の時に C0BPacketEntityAction をキャンセル
        if (hasReceivedVelocity && packet is C0BPacketEntityAction) {
            hasReceivedVelocity = false
            event.cancelEvent()
        }
    }
}