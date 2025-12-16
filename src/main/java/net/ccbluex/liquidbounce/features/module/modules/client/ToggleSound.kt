package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.ModuleToggleEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.ListValue
import net.minecraft.client.audio.SoundHandler
import net.minecraft.client.audio.SoundManager
import paulscode.sound.SoundSystem
import paulscode.sound.SoundSystemConfig

@ModuleInfo(name = "ToggleSound", category = ModuleCategory.CLIENT)
class ToggleSound : Module() {

    private val soundType = ListValue("SoundType", arrayOf("Epic", "QuickMacro"), "Epic")

    private var lastEnableTime = 0L
    private var lastDisableTime = 0L
    private var soundCounter = 0

    private var sndManagerField: java.lang.reflect.Field? = null
    private var sndSystemField: java.lang.reflect.Field? = null

    private fun getSoundSystem(): SoundSystem? {
        return try {
            if (sndManagerField == null) {
                sndManagerField = SoundHandler::class.java.getDeclaredField("sndManager")
                sndManagerField!!.isAccessible = true
            }
            val sndManager = sndManagerField!!.get(mc.soundHandler) as? SoundManager ?: return null

            if (sndSystemField == null) {
                sndSystemField = SoundManager::class.java.getDeclaredField("sndSystem")
                sndSystemField!!.isAccessible = true
            }
            sndSystemField!!.get(sndManager) as? SoundSystem
        } catch (e: Exception) {
            null
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onModuleToggle(event: ModuleToggleEvent) {
        if (!state || event.module == this) return

        val now = System.currentTimeMillis()
        val isEnable = event.module.state

        if (isEnable) {
            if (now - lastEnableTime < 50L) return
            lastEnableTime = now
        } else {
            if (now - lastDisableTime < 50L) return
            lastDisableTime = now
        }

        val action = if (isEnable) "enable" else "disable"
        val type = soundType.get().lowercase()
        val path = "assets/crosssine/sounds/toggle/$type/$action.ogg"

        val url = javaClass.classLoader.getResource(path) ?: return
        val sndSystem = getSoundSystem() ?: return

        val source = "toggle_${soundCounter++}"
        sndSystem.newSource(false, source, url, path, false, 0f, 0f, 0f, SoundSystemConfig.ATTENUATION_NONE, 0f)
        sndSystem.setVolume(source, 1f)
        sndSystem.play(source)
    }
}