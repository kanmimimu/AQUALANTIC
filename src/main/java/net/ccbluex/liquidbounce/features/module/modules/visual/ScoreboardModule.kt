package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Mouse
import java.awt.Color

@ModuleInfo(name = "Scoreboard", category = ModuleCategory.VISUAL, defaultOn = true)
object ScoreboardModule : Module() {

    @JvmField
    val textShadow = BoolValue("Text-Shadow", false)
    @JvmField
    val showNumber = BoolValue("Show-Number", true)
    @JvmField
    val changedDomain = BoolValue("Changed-Domain", false)
    @JvmField
    val roundMode = BoolValue("RoundedMode", true)//TODO MICA FOR EVERYONE!

    @JvmField
    var draging = false
    @JvmField
    var posY = 0f
    @JvmField
    var posX = -5.0f
    @JvmField
    var dragOffsetX = 0f
    @JvmField
    var dragOffsetY = 0f
    private var ux_size = 0f
    private var uy_size = 0f
    private var ux2_size = 0f
    private var uy2_size = 0f
    private var outlineProgress = 0f

    val rounded: Boolean
        get() = roundMode.get()

    val shadow: Boolean
        get() = textShadow.get()

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val mouseX = if (mc.currentScreen == null) 0 else Mouse.getX() * mc.currentScreen!!.width / mc.displayWidth
        val mouseY = if (mc.currentScreen == null) 0 else mc.currentScreen!!.height - Mouse.getY() * mc.currentScreen!!.height / mc.displayHeight - 1

        if (draging) {
            posX = mouseX - dragOffsetX
            posY = mouseY - dragOffsetY
            if (!Mouse.isButtonDown(0) || mc.currentScreen == null) {
                draging = false
            }
        }

        outlineProgress += 0.00375f * RenderUtils.deltaTime * (if (mc.currentScreen is GuiChat && MouseUtils.mouseWithinBounds(mouseX, mouseY, ux_size, uy_size, ux2_size, uy2_size)) 1.0f else -1.0f)
        outlineProgress = outlineProgress.coerceIn(0.0f, 1.0f)

        GlStateManager.pushMatrix()
        if (outlineProgress > 0.0f) {
            RenderUtils.drawRoundedOutline(ux_size, uy_size, ux2_size, uy2_size, 7.0f, 2.5f, Color(255, 255, 255, (255 * outlineProgress).toInt()).rgb)
        }
        GlStateManager.popMatrix()
        GlStateManager.resetColor()
    }

    val alpha: Int
        get() = 150
}
