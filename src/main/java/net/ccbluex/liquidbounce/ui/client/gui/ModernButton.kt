package net.ccbluex.liquidbounce.ui.client.gui

import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import java.awt.Color

class ModernButton(val id: Int, var x: Int, var y: Int, var width: Int, var height: Int, val text: String) : MinecraftInstance() {

    var enabled = true
    private var hoverProgress = 0f

    fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val isHovered = isHovered(mouseX, mouseY) && enabled

        // Animate hover
        val fps = Minecraft.getDebugFPS().toFloat()
        if (fps > 0) { // Avoid division by zero
            if (isHovered) {
                if (hoverProgress < 1f) hoverProgress += 0.1f * (1f / fps) * 100
                if (hoverProgress > 1f) hoverProgress = 1f
            } else {
                if (hoverProgress > 0f) hoverProgress -= 0.1f * (1f / fps) * 100
                if (hoverProgress < 0f) hoverProgress = 0f
            }
        }
        
        val animatedWidth = width + hoverProgress * 10
        val animatedHeight = height + hoverProgress * 5
        val animatedX = x - (hoverProgress * 5).toInt()
        val animatedY = y - (hoverProgress * 2.5).toInt()

        // Draw button background
        val backgroundColor = if (enabled) Color(40, 40, 40, 180) else Color(80, 80, 80, 150)
        RenderUtils.drawRoundedRect(animatedX.toFloat(), animatedY.toFloat(), (animatedX + animatedWidth).toFloat(), (animatedY + animatedHeight).toFloat(), 8f, backgroundColor.rgb)

        // Draw button text
        val textColor = if (enabled) Color.WHITE.rgb else Color.LIGHT_GRAY.rgb
        val font = Fonts.font35
        font.drawCenteredString(text, x + width / 2f, y + height / 2f - font.FONT_HEIGHT / 2f + 1, textColor, true)
    }

    fun isHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height
    }
}