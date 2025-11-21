package net.ccbluex.liquidbounce.features.module.modules.movement.noslows.impl

import net.ccbluex.liquidbounce.features.module.modules.movement.noslows.NoSlowMode
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemSword

class ZenticNoSlow : NoSlowMode("Zentic") {
    override fun slow(): Float {
        if (mc.thePlayer.isUsingItem) {
            val item = mc.thePlayer.itemInUse?.item
            if (item is ItemSword || item is ItemBow) {
                return 1F
            }
        }
        return 0.2F
    }
}