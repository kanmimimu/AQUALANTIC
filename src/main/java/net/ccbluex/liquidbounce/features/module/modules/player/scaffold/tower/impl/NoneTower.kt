package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.minecraft.client.Minecraft


class NoneTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {

        return false
    }
}
