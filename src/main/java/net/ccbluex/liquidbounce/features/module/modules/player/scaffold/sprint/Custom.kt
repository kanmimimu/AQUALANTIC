package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

import net.ccbluex.liquidbounce.utils.MovementUtils

class Custom(
    private val sprintCustom: () -> Boolean,
    private val strafeCustom: () -> Boolean,
    private val strafeSpeedCustom: () -> Boolean,
    private val strafeSpeedCustomValue: () -> Float
) : Sprint("Custom") {
    override fun isActive(): Boolean = sprintCustom()
    
    override fun onTick() {
        if (strafeCustom()) {
            if (strafeSpeedCustom()) {
                MovementUtils.strafe(strafeSpeedCustomValue())
            } else {
                MovementUtils.strafe()
            }
        }
    }
}
