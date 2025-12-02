# Scaffold モジュール - 開発者リファレンス

## ファイル構成

```
scaffold/
├── Scaffold.kt              # メインモジュール
├── ScaffoldConstants.kt      # 定数定義
├── ScaffoldRotationManager.kt # Rotation処理
└── ScaffoldTowerHelper.kt    # Tower移動処理
```

## ScaffoldConstants.kt

### Rotation定数
```kotlin
const val DEFAULT_ROTATION_PITCH = 85F
const val GODBRIGE_ROTATION_PITCH = 75F
const val GODBRIGE_ALT_ROTATION_PITCH = 78F
```

### Motion定数
```kotlin
const val JUMP_MOTION = 0.42
const val JUMP_MOTION_ALT = 0.4191
const val JUMP_MOTION_LOW = 0.4
const val JUMP_MOTION_AAC = 0.41998
const val BLOCKSMC_MOTION_DOWN = -0.0784000015258789
```

### Yaw定数
```kotlin
const val YAW_WRAP_ANGLE = 180F
const val YAW_STEP_45 = 45F
const val YAW_STEP_90 = 90F
const val YAW_STEP_135 = 135F
const val YAW_DIAGONAL_MIN = 20.0F
const val YAW_DIAGONAL_MAX = 70.0F
```

### その他の定数
```kotlin
const val RAY_TRACE_DISTANCE = 5.0
const val MAX_REACH_DISTANCE_SQ = 18.0
const val OFFSET_STEP = 0.1
const val OFFSET_MAX = 0.9
const val ZITTER_DELAY_MS = 100L
```

## ScaffoldRotationManager.kt

### データクラス

#### PlaceRotation
```kotlin
data class PlaceRotation(
    val placeInfo: PlaceInfo,
    val rotation: Rotation
)
```

### 関数

#### resetSpinYaw()
```kotlin
fun resetSpinYaw()
```
Spin yawカウンターを0にリセットします。

#### calculateRotationForMode()
```kotlin
fun calculateRotationForMode(
    mode: String,
    placeRotation: PlaceRotation?,
    lockRotation: Rotation?,
    bridgeMode: String,
    isLookingDiagonally: Boolean,
    towerStatus: Boolean,
    watchdogTellyValue: Boolean,
    watchDogDelay: Int,
    watchdogBoostValue: Boolean,
    keyBindUseItem: Boolean,
    prevTowered: Boolean,
    shouldPlace: Boolean
): Rotation?
```
静的Rotationモード用のRotationを計算します（`rotationStatic()`で使用）。

**パラメータ:**
- `mode` - Rotationモード名
- `lockRotation` - 現在のロックされたRotation
- `bridgeMode` - 現在のブリッジモード
- `isLookingDiagonally` - プレイヤーが斜めを向いているか
- `towerStatus` - Towerが有効か
- その他のパラメータは特定のモード動作を制御

**戻り値:** 計算されたRotation、または静的Rotationを使用しない場合はnull

**対応モード:**
- `stabilized` - lockRotationまたはmovingYaw + 180のyawを返す
- `watchdog` - WatchDog互換Rotation
- `watchdog2` - 斜め移動対応の改良版WatchDog Rotation
- `telly` - TellyブリッジRotation
- `spin` - 増分するSpin Rotation

#### calculateSearchRotation()
```kotlin
fun calculateSearchRotation(
    mode: String,
    placeRotation: PlaceRotation,
    bridgeMode: String,
    isLookingDiagonally: Boolean,
    towerStatus: Boolean,
    watchdogTellyValue: Boolean,
    watchDogDelay: Int,
    watchdogBoostValue: Boolean,
    keyBindUseItem: Boolean,
    prevTowered: Boolean,
    shouldPlace: Boolean,
    steps4590: ArrayList<Float>
): Rotation
```
ブロック配置位置検索時のRotationを計算します。

**パラメータ:**
- `mode` - Rotationモード名
- `placeRotation` - 検出された配置Rotation
- `steps4590` - 有効なyawステップ配列
- その他のパラメータは`calculateRotationForMode()`と同じ

**戻り値:** モードに基づいて計算されたRotation

**対応モード:**
- `normal` - placeRotation.rotationをそのまま返す
- `stabilized` - 45°間隔で安定化されたyawを返す
- `watchdog` - WatchDog互換Rotation
- `watchdog2` - 改良版WatchDog Rotation
- `telly` - fixYawを使用したTellyブリッジRotation
- `snap` - MovingYaw + 180のRotation
- `spin` - 増分するSpin Rotation

#### calculateRotation()
```kotlin
fun calculateRotation(eyesPos: Vec3, hitVec: Vec3): Rotation
```
目の位置とヒットベクトルからRotationに変換します。

**パラメータ:**
- `eyesPos` - プレイヤーの目の位置
- `hitVec` - ターゲットのヒット位置

**戻り値:** eyesPosからhitVecを見るためのRotation

#### isValidBlockRotation()
```kotlin
fun isValidBlockRotation(
    neighbor: BlockPos,
    eyesPos: Vec3,
    rotation: Rotation,
    mc: Minecraft
): Boolean
```
Rotationがターゲットブロックを見ることができるか検証します。

**パラメータ:**
- `neighbor` - ターゲットブロック位置
- `eyesPos` - プレイヤーの目の位置
- `rotation` - 検証するRotation
- `mc` - Minecraftインスタンス

**戻り値:** Rotationからのray traceがターゲットブロックに当たる場合true

#### generateOffsets()
```kotlin
fun generateOffsets(): Sequence<Vec3>
```
ブロック配置オフセット位置を生成します。

**戻り値:** 0.1から0.9まで0.1刻みのVec3オフセットのSequence（xyz）

## ScaffoldTowerHelper.kt

### 関数

#### resetTowerTick()
```kotlin
fun resetTowerTick()
```
Towerティックカウンターを0にリセットします。

#### doTowerMove()
```kotlin
fun doTowerMove(mode: String, mc: Minecraft)
```
モードに基づいてTower移動を実行します。

**パラメータ:**
- `mode` - Towerモード名（大文字小文字区別なし）
- `mc` - Minecraftインスタンス

**対応モード:**

**ncp**
- Strafe移動
- posY % 1 <= 0.00153598の時、位置をfloorに設定
- motionYを0.42に設定

**blocksmc**
- Strafe移動
- 地上でジャンプ（motionY = 0.42）

**vanilla**
- motionYを0.42に直接設定

**lowhop**
- Strafe移動
- 地上でジャンプ（motionY = 0.4）

**fastjump**
- Strafe移動
- motionY < 0の時ジャンプ
- Jump Boostポーション効果がある場合追加

**aac**
- Strafe移動
- posY % 1 <= 0.005の時、位置をfloorに設定
- motionYを0.41998に設定

**extra**
- Motion調整を伴う4ティックサイクル
- towerTick状態変数を使用

## Scaffold.kt - 主要関数

### イベントハンドラ

#### onEnable()
```kotlin
override fun onEnable()
```
モジュール有効化時に呼び出されます。状態変数を初期化し、ヘルパーをリセットします。

#### onTick()
```kotlin
fun onTick(event: TickEvent)
```
メインティックハンドラ。Y座標、ブリッジモード、Sprint、Eagle、Zitter、Tower、配置を管理します。

#### onMotion()
```kotlin
fun onMotion(event: MotionEvent)
```
Motionイベントを処理します。AutoBlockの切り替え、Tower状態、Rotationを管理します。

#### onMove()
```kotlin
fun onMove(event: MoveEvent)
```
移動イベントを処理します。カスタムSprint motionとSafe walkを適用します。

#### onPacket()
```kotlin
fun onPacket(event: PacketEvent)
```
パケットイベントを処理します。C08パケットを修正し、Sprintパケットキャンセルを管理します。

### コアロジック関数

#### findBlock()
```kotlin
private fun findBlock(expand: Boolean)
```
ブリッジモードと設定に基づいてブロック配置位置を検索します。

#### search()
```kotlin
private fun search(blockPosition: BlockPos, checks: Boolean): Boolean
```
指定されたブロック位置周辺で有効な配置位置を検索します。

**パラメータ:**
- `blockPosition` - 検索の中心位置
- `checks` - Ray traceと距離チェックを実行するか

**戻り値:** 有効な配置が見つかりlockRotationが設定された場合true

#### place()
```kotlin
private fun place()
```
条件が満たされている場合、ブロック配置を実行します。

#### shouldPlace()
```kotlin
private fun shouldPlace(): Boolean
```
遅延とブリッジモードに基づいてブロックを配置すべきか判断します。

**戻り値:** 配置を実行すべき場合true

#### move()
```kotlin
private fun move()
```
ScaffoldTowerHelperに委譲してTower移動を実行します。

### ユーティリティ関数

#### rotationStatic()
```kotlin
private fun rotationStatic()
```
ScaffoldRotationManagerを使用して静的Rotationを計算・適用します。

#### canRotation()
```kotlin
private fun canRotation(): Boolean
```
Rotationを適用すべきか確認します。

**戻り値:** RotationモードがNoneまたはSpin以外でlockRotationが設定されている場合true

#### getSpeed()
```kotlin
fun getSpeed(): Float
```
Speedポーション効果に基づいて移動速度を計算します。

**戻り値:** 速度値（0.42F、0.5F、または0.53F）

#### onGround()
```kotlin
private fun onGround(): Boolean
```
プレイヤーが地上にいるか確認します。

**戻り値:** player.onGroundまたはoffGroundTicks == 0の場合true

## 定数使用例

```kotlin
// Rotation定数の使用
val rotation = Rotation(yaw, DEFAULT_ROTATION_PITCH)

// Motion定数の使用
player.motionY = JUMP_MOTION

// Yaw定数の使用
val fixedYaw = round(yaw / YAW_STEP_45) * YAW_STEP_45
```

## Rotation計算フロー

```
1. search() が配置位置を検索
   ↓
2. ScaffoldRotationManager.calculateRotation() がRotationに変換
   ↓
3. ScaffoldRotationManager.isValidBlockRotation() が検証
   ↓
4. ScaffoldRotationManager.calculateSearchRotation() がモード適用
   ↓
5. RotationUtils.limitAngleChange() が遷移を滑らかにする
   ↓
6. RotationUtils.setTargetRotationReverse() がRotationを適用
```

## Tower移動フロー

```
1. onTick() がtowerStatusをチェック（ジャンプキー押下）
   ↓
2. towerStatusがtrueの場合move()を呼び出し
   ↓
3. ScaffoldTowerHelper.doTowerMove() がモードに基づいて実行
   ↓
4. 水平/垂直に基づいてTimer調整を適用
```
