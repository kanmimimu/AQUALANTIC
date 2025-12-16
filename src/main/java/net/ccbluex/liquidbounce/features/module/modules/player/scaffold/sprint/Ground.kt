package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

class Ground : Sprint("Ground") {
    override fun isActive(): Boolean = mc.thePlayer.onGround
}
