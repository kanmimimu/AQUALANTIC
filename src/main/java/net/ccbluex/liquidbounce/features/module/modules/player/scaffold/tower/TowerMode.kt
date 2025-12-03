package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

import net.minecraft.client.Minecraft

/**
 * タワーモードの動作を定義するインターフェース
 * 
 * 各タワーモードがこのインターフェースを実装し、独自の移動ロジックを提供する
 */
interface TowerMode {
    /**
     * タワー移動を実行
     * 
     * @param mc Minecraft インスタンス
     * @return タワー移動が実行されたかどうか
     */
    fun execute(mc: Minecraft): Boolean
}
