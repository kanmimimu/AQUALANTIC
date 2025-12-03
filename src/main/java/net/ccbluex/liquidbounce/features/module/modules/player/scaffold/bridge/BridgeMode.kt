package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.minecraft.client.Minecraft


interface BridgeMode {

    fun isActive(mc: Minecraft): Boolean
    

    fun execute(mc: Minecraft, towerStatus: Boolean): Boolean
    

    fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean
    

    fun shouldKeepY(): Boolean
    

    fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double?
}
