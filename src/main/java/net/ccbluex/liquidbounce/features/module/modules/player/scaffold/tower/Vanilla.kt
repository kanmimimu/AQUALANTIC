package net.ccbluex.liquidbounce.features.module.modules.player.scaffold.tower

class Vanilla : Tower("Vanilla") {
    override fun onMove() {
        mc.thePlayer.motionY = 0.42
    }
}
