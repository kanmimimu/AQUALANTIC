package net.ccbluex.liquidbounce.event

import net.ccbluex.liquidbounce.features.module.Module

/**
 * Called when module toggled
 */
class ModuleToggleEvent(val module: Module) : Event()
