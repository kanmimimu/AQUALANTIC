package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.minecraft.client.Minecraft


interface TowerMode {

    fun execute(mc: Minecraft): Boolean
}
