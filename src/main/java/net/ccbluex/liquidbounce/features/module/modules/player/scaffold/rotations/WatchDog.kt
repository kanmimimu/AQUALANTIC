package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.rotations

import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.minecraft.client.settings.GameSettings

class WatchDog(
    private val watchDogDelayProvider: () -> Int,
    private val towerStatusProvider: () -> Boolean,
    private val watchdogBoostProvider: () -> Boolean,
    private val watchdogTellyProvider: () -> Boolean,
    private val bridgeModeProvider: () -> String
) : ScaffoldRotation("WatchDog") {
    override fun getRotation(lockRotation: Rotation?, defaultPitch: Float): Rotation {
        val isTellyActive = watchDogDelayProvider() > PlayerUtils.offGroundTicks &&
                !towerStatusProvider() &&
                (!watchdogBoostProvider() || !GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem)) &&
                watchdogTellyProvider() &&
                bridgeModeProvider().equals("WatchDog", true)

        val yaw = if (isTellyActive) {
            MovementUtils.movingYaw
        } else {
            lockRotation?.yaw ?: (MovementUtils.movingYaw - 180)
        }
        val pitch = lockRotation?.pitch ?: defaultPitch
        return Rotation(yaw, pitch)
    }
}

