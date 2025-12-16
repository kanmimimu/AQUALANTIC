package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

import net.ccbluex.liquidbounce.utils.PlayerUtils

class WatchDog(
    private val watchDogDelay: () -> Int,
    private val towerStatus: () -> Boolean,
    private val isWatchDogBridge: () -> Boolean
) : Sprint("WatchDog") {
    override fun isActive(): Boolean {
        return PlayerUtils.offGroundTicks < watchDogDelay() && !towerStatus() && isWatchDogBridge()
    }
}
