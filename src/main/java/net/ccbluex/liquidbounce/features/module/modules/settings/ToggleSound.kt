package net.ccbluex.liquidbounce.features.module.modules.settings

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.ModuleToggleEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.ListValue
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

@ModuleInfo(name = "ToggleSound", category = ModuleCategory.SETTING)
class ToggleSound : Module() {

    private val soundType = ListValue("SoundType", arrayOf("Epic", "QuickMacro"), "Epic")

    @EventTarget
    fun onModuleToggle(event: ModuleToggleEvent) {
        val soundName = if (event.module.state) "enable" else "disable"
        val type = soundType.get().lowercase()
        val key = "toggle.$type.$soundName"
        
        ClientUtils.logInfo("Playing sound: $key")
        mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("minecraft", key)))
    }
}
