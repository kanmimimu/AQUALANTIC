package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

class Air : Sprint("Air") {
    override fun isActive(): Boolean = !mc.thePlayer.onGround
}
