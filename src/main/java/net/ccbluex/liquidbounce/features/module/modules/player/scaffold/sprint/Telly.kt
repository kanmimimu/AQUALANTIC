package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.sprint

class Telly(private val shouldPlace: () -> Boolean) : Sprint("Telly") {
    override fun isActive(): Boolean = !shouldPlace()
}
