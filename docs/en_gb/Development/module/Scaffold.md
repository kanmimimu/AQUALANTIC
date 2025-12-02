# Scaffold Module - Developer Reference

## File Structure

```
scaffold/
├── Scaffold.kt              # Main module
├── ScaffoldConstants.kt      # Constants definitions
├── ScaffoldRotationManager.kt # Rotation logic
└── ScaffoldTowerHelper.kt    # Tower movement logic
```

## ScaffoldConstants.kt

### Rotation Constants
```kotlin
const val DEFAULT_ROTATION_PITCH = 85F
const val GODBRIGE_ROTATION_PITCH = 75F
const val GODBRIGE_ALT_ROTATION_PITCH = 78F
```

### Motion Constants
```kotlin
const val JUMP_MOTION = 0.42
const val JUMP_MOTION_ALT = 0.4191
const val JUMP_MOTION_LOW = 0.4
const val JUMP_MOTION_AAC = 0.41998
const val BLOCKSMC_MOTION_DOWN = -0.0784000015258789
```

### Yaw Constants
```kotlin
const val YAW_WRAP_ANGLE = 180F
const val YAW_STEP_45 = 45F
const val YAW_STEP_90 = 90F
const val YAW_STEP_135 = 135F
const val YAW_DIAGONAL_MIN = 20.0F
const val YAW_DIAGONAL_MAX = 70.0F
```

### Other Constants
```kotlin
const val RAY_TRACE_DISTANCE = 5.0
const val MAX_REACH_DISTANCE_SQ = 18.0
const val OFFSET_STEP = 0.1
const val OFFSET_MAX = 0.9
const val ZITTER_DELAY_MS = 100L
```

## ScaffoldRotationManager.kt

### Data Classes

#### PlaceRotation
```kotlin
data class PlaceRotation(
    val placeInfo: PlaceInfo,
    val rotation: Rotation
)
```

### Functions

#### resetSpinYaw()
```kotlin
fun resetSpinYaw()
```
Resets the spin yaw counter to 0.

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
Calculates rotation for static rotation modes (used in `rotationStatic()`).

**Parameters:**
- `mode` - Rotation mode name
- `lockRotation` - Current locked rotation
- `bridgeMode` - Current bridge mode
- `isLookingDiagonally` - Whether player is looking diagonally
- `towerStatus` - Whether tower is active
- Other parameters control specific mode behaviors

**Returns:** Calculated rotation or null if mode doesn't use static rotation

**Supported Modes:**
- `stabilized` - Returns yaw from lockRotation or movingYaw + 180
- `watchdog` - WatchDog compatible rotation
- `watchdog2` - Improved WatchDog rotation with diagonal support
- `telly` - Telly bridge rotation
- `spin` - Incrementing spin rotation

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
Calculates rotation when searching for block placement position.

**Parameters:**
- `mode` - Rotation mode name
- `placeRotation` - Found placement rotation
- `steps4590` - Valid yaw steps array
- Other parameters same as `calculateRotationForMode()`

**Returns:** Calculated rotation based on mode

**Supported Modes:**
- `normal` - Returns placeRotation.rotation as-is
- `stabilized` - Returns stabilized yaw at 45° intervals
- `watchdog` - WatchDog compatible rotation
- `watchdog2` - Improved WatchDog rotation
- `telly` - Telly bridge rotation with fixYaw
- `snap` - MovingYaw + 180 rotation
- `spin` - Incrementing spin rotation

#### calculateRotation()
```kotlin
fun calculateRotation(eyesPos: Vec3, hitVec: Vec3): Rotation
```
Converts eye position and hit vector to rotation.

**Parameters:**
- `eyesPos` - Player's eye position
- `hitVec` - Target hit position

**Returns:** Rotation to look at hitVec from eyesPos

#### isValidBlockRotation()
```kotlin
fun isValidBlockRotation(
    neighbor: BlockPos,
    eyesPos: Vec3,
    rotation: Rotation,
    mc: Minecraft
): Boolean
```
Validates if rotation can see the target block.

**Parameters:**
- `neighbor` - Target block position
- `eyesPos` - Player's eye position
- `rotation` - Rotation to validate
- `mc` - Minecraft instance

**Returns:** True if ray trace from rotation hits target block

#### generateOffsets()
```kotlin
fun generateOffsets(): Sequence<Vec3>
```
Generates block placement offset positions.

**Returns:** Sequence of Vec3 offsets from 0.1 to 0.9 in 0.1 increments (xyz)

## ScaffoldTowerHelper.kt

### Functions

#### resetTowerTick()
```kotlin
fun resetTowerTick()
```
Resets the tower tick counter to 0.

#### doTowerMove()
```kotlin
fun doTowerMove(mode: String, mc: Minecraft)
```
Executes tower movement based on mode.

**Parameters:**
- `mode` - Tower mode name (case-insensitive)
- `mc` - Minecraft instance

**Supported Modes:**

**ncp**
- Strafe movement
- Sets position to floor when posY % 1 <= 0.00153598
- Sets motionY to 0.42

**blocksmc**
- Strafe movement
- Jump when on ground (motionY = 0.42)

**vanilla**
- Sets motionY to 0.42 directly

**lowhop**
- Strafe movement
- Jump when on ground (motionY = 0.4)

**fastjump**
- Strafe movement
- Jump when motionY < 0
- Adds jump boost potion effect if active

**aac**
- Strafe movement
- Sets position to floor when posY % 1 <= 0.005
- Sets motionY to 0.41998

**extra**
- Complex 4-tick cycle with motion adjustments
- Uses towerTick state variable

## Scaffold.kt - Key Functions

### Event Handlers

#### onEnable()
```kotlin
override fun onEnable()
```
Called when module is enabled. Initializes state variables and resets helpers.

#### onTick()
```kotlin
fun onTick(event: TickEvent)
```
Main tick handler. Manages Y position, bridge modes, sprint, eagle, zitter, tower, and placement.

#### onMotion()
```kotlin
fun onMotion(event: MotionEvent)
```
Handles motion events. Manages auto-block switching, tower status, and rotations.

#### onMove()
```kotlin
fun onMove(event: MoveEvent)
```
Handles movement events. Applies custom sprint motion and safe walk.

#### onPacket()
```kotlin
fun onPacket(event: PacketEvent)
```
Handles packet events. Modifies C08 packets and manages sprint packet cancellation.

### Core Logic Functions

#### findBlock()
```kotlin
private fun findBlock(expand: Boolean)
```
Searches for block placement position based on bridge mode and settings.

#### search()
```kotlin
private fun search(blockPosition: BlockPos, checks: Boolean): Boolean
```
Searches for valid placement position around given block position.

**Parameters:**
- `blockPosition` - Center position to search around
- `checks` - Whether to perform ray trace and distance checks

**Returns:** True if valid placement found and lockRotation set

#### place()
```kotlin
private fun place()
```
Executes block placement if conditions are met.

#### shouldPlace()
```kotlin
private fun shouldPlace(): Boolean
```
Determines if block should be placed based on delays and bridge mode.

**Returns:** True if placement should occur

#### move()
```kotlin
private fun move()
```
Executes tower movement by delegating to ScaffoldTowerHelper.

### Utility Functions

#### rotationStatic()
```kotlin
private fun rotationStatic()
```
Calculates and applies static rotation using ScaffoldRotationManager.

#### canRotation()
```kotlin
private fun canRotation(): Boolean
```
Checks if rotation should be applied.

**Returns:** True if rotation mode is not "None" or "Spin" and lockRotation is set

#### getSpeed()
```kotlin
fun getSpeed(): Float
```
Calculates movement speed based on speed potion effect.

**Returns:** Speed value (0.42F, 0.5F, or 0.53F)

#### onGround()
```kotlin
private fun onGround(): Boolean
```
Checks if player is on ground.

**Returns:** True if player.onGround or offGroundTicks == 0

## Constants Usage Example

```kotlin
// Using rotation constants
val rotation = Rotation(yaw, DEFAULT_ROTATION_PITCH)

// Using motion constants
player.motionY = JUMP_MOTION

// Using yaw constants
val fixedYaw = round(yaw / YAW_STEP_45) * YAW_STEP_45
```

## Rotation Calculation Flow

```
1. search() finds placement position
   ↓
2. ScaffoldRotationManager.calculateRotation() converts to rotation
   ↓
3. ScaffoldRotationManager.isValidBlockRotation() validates
   ↓
4. ScaffoldRotationManager.calculateSearchRotation() applies mode
   ↓
5. RotationUtils.limitAngleChange() smooths transition
   ↓
6. RotationUtils.setTargetRotationReverse() applies rotation
```

## Tower Movement Flow

```
1. onTick() checks towerStatus (jump key held)
   ↓
2. move() called if towerStatus is true
   ↓
3. ScaffoldTowerHelper.doTowerMove() executes based on mode
   ↓
4. Timer adjustments applied based on horizontal/vertical
```
