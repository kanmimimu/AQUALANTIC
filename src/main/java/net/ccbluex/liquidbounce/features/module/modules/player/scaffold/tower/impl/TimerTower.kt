package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.impl

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower.TowerMode
import net.minecraft.client.Minecraft

/**
 * Timer タワーモード
 * タイマーを使用したタワー動作
 * （実際のタイマー処理はScaffold.kt側で管理される）
 */
class TimerTower : TowerMode {
    override fun execute(mc: Minecraft): Boolean {
        // タイマー処理はScaffold.kt側で行うため、ここでは何もしない
        return false
    }
}
