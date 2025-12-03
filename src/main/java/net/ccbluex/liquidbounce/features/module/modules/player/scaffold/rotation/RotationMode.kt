package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotation

import net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data.PlaceRotation
import net.ccbluex.liquidbounce.utils.Rotation

/**
 * ローテーションモードの動作を定義するインターフェース
 * 
 * 各ローテーションモードがこのインターフェースを実装し、独自の回転計算ロジックを提供する
 */
interface RotationMode {
    /**
     * 静的な状況でのローテーションを計算
     * （ターゲットブロックがない状態）
     * 
     * @param lockRotation 現在のロックローテーション
     * @param bridgeMode 使用中のブリッジモード
     * @param towerStatus タワー状態
     * @param isLookingDiagonally 斜め方向を見ているか
     * @param prevTowered 前回タワーしたか
     * @param shouldPlace ブロックを配置すべきか
     * @return 計算されたローテーション、またはnull
     */
    fun calculateStaticRotation(
        lockRotation: Rotation?,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean
    ): Rotation?
    
    /**
     * 検索時のローテーションを計算
     * （ターゲットブロックが見つかった状態）
     * 
     * @param placeRotation 配置ローテーション情報
     * @param bridgeMode 使用中のブリッジモード
     * @param towerStatus タワー状態
     * @param isLookingDiagonally 斜め方向を見ているか
     * @param prevTowered 前回タワーしたか
     * @param shouldPlace ブロックを配置すべきか
     * @param steps4590 45/90度のステップリスト
     * @return 計算されたローテーション
     */
    fun calculateSearchRotation(
        placeRotation: PlaceRotation,
        bridgeMode: String,
        towerStatus: Boolean,
        isLookingDiagonally: Boolean,
        prevTowered: Boolean,
        shouldPlace: Boolean,
        steps4590: ArrayList<Float>
    ): Rotation
}
