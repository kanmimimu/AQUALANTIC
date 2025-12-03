package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.data

import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.block.PlaceInfo

/**
 * ブロック配置のための回転情報を保持するデータクラス
 * 
 * @param placeInfo ブロック配置情報
 * @param rotation 必要な回転角度
 */
data class PlaceRotation(val placeInfo: PlaceInfo, val rotation: Rotation)
