package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.bridge

import net.minecraft.client.Minecraft

/**
 * ブリッジモードの動作を定義するインターフェース
 * 
 * 各ブリッジモード(Normal、Andromeda、Telly、WatchDog、GodBridge、KeepUP、Keep-Y)が
 * このインターフェースを実装し、独自のロジックを提供する
 */
interface BridgeMode {
    /**
     * このブリッジモードがアクティブかどうかをチェック
     * 
     * @param mc Minecraft インスタンス
     * @return アクティブであればtrue
     */
    fun isActive(mc: Minecraft): Boolean
    
    /**
     * ブリッジモードの動作を実行
     * 
     * @param mc Minecraft インスタンス
     * @param towerStatus タワー状態
     * @return ブリッジロジックが実行されたかどうか
     */
    fun execute(mc: Minecraft, towerStatus: Boolean): Boolean
    
    /**
     * ブロック配置が許可されるかどうか
     * 
     * @param offGroundTicks 地面から離れたティック数
     * @param towerStatus タワー状態
     * @param prevTowered 前回タワーしたかどうか
     * @return 配置が許可されればtrue
     */
    fun shouldPlace(offGroundTicks: Int, towerStatus: Boolean, prevTowered: Boolean): Boolean
    
    /**
     * 特定の Y 座標を保持すべきか
     * 
     * @return Y座標を保持すべきであればtrue
     */
    fun shouldKeepY(): Boolean
    
    /**
     * ブリッジモード用の特別な配置Y座標を取得
     * 
     * @param mc Minecraft インスタンス
     * @param lastGroundY 最後の地面のY座標
     * @return 配置すべきY座標、特別な処理がない場合null
     */
    fun getPlaceY(mc: Minecraft, lastGroundY: Int?): Double?
}
