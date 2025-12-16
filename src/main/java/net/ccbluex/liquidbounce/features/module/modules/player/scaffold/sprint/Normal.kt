package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

class Normal(
    private val isBlocksMCTower: () -> Boolean
) : Sprint("Normal") {
    override fun isActive(): Boolean = !isBlocksMCTower()
}
