package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.CrossSine
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

@ModuleInfo(name = "ToggleSound", category = ModuleCategory.CLIENT, defaultOn = true)
class ToggleSound : Module() {

    private val soundType = ListValue("SoundType", arrayOf("Epic", "QuickMacro"), "Epic")

    private var lastEnableTime = 0L
    private var lastDisableTime = 0L
    private var soundCounter = 0
    private var cachedSoundSystem: SoundSystem? = null

    private fun getSoundSystem(): SoundSystem? {
        cachedSoundSystem?.let { return it }

        return runCatching {
            SoundHandler::class.java.declaredFields.asSequence()
                .onEach { it.isAccessible = true }
                .mapNotNull { it.get(mc.soundHandler) as? SoundManager }
                .flatMap { manager ->
                    SoundManager::class.java.declaredFields.asSequence()
                        .onEach { it.isAccessible = true }
                        .mapNotNull { it.get(manager) as? SoundSystem }
                }
                .firstOrNull()
                ?.also { cachedSoundSystem = it }
        }.getOrNull()
    }

    @EventTarget(ignoreCondition = true)
    fun onModuleToggle(event: ModuleToggleEvent) {
        if (CrossSine.isStarting || !state || event.module == this) return

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
        val path = "/assets/crosssine/sounds/toggle/${soundType.get().lowercase()}/$action.ogg"

        val url = ToggleSound::class.java.getResource(path) ?: return
        val system = getSoundSystem() ?: return
        val source = "toggle_${soundCounter++}"

        system.newSource(false, source, url, path, false, 0f, 0f, 0f, SoundSystemConfig.ATTENUATION_NONE, 0f)
        system.setVolume(source, 1f)
        system.play(source)
    }
}