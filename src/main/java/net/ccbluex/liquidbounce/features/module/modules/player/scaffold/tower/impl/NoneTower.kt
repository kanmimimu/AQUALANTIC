package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.minecraft.client.Minecraft

/**
 * None タワーモード
 * タワー機能を使用しない
 */
class NoneTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        // 何もしない
        return false
    }
}
