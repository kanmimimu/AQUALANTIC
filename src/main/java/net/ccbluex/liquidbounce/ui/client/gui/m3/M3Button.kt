package net.ccbluex.liquidbounce.ui.client.gui.m3

import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3.M3Colors
import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3.M3Dimensions
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import java.awt.Color

enum class M3ButtonStyle {
    FILLED,
    FILLED_TONAL,
    TEXT
}

class M3Button(
    @JvmField val id: Int,
    @JvmField var x: Float,
    @JvmField var y: Float,
    @JvmField var width: Float,
    @JvmField var height: Float,
    @JvmField val text: String,
    @JvmField val style: M3ButtonStyle = M3ButtonStyle.FILLED_TONAL
) : MinecraftInstance() {

    @JvmField var enabled = true
    private var hoverProgress = 0f
    private var pressProgress = 0f
    private var isPressed = false
    
    fun getId(): Int = id
    fun getX(): Float = x
    fun getY(): Float = y
    fun setY(value: Float) { y = value }

    fun draw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val isHovered = isHovered(mouseX, mouseY) && enabled
        
        val deltaTime = RenderUtils.deltaTime.toFloat().coerceIn(1f, 50f)
        val animSpeed = 0.008f * deltaTime
        
        if (isHovered) {
            hoverProgress = (hoverProgress + animSpeed).coerceAtMost(1f)
        } else {
            hoverProgress = (hoverProgress - animSpeed).coerceAtLeast(0f)
        }
        
        if (isPressed) {
            pressProgress = (pressProgress + animSpeed * 2).coerceAtMost(1f)
        } else {
            pressProgress = (pressProgress - animSpeed * 2).coerceAtLeast(0f)
        }

        val backgroundColor: Color
        val textColor: Color
        val cornerRadius = height / 2f
        
        when (style) {
            M3ButtonStyle.FILLED -> {
                backgroundColor = if (!enabled) {
                    M3Colors.withAlpha(M3Colors.onSurface, 30)
                } else {
                    applyStateLayer(M3Colors.primary, hoverProgress, pressProgress)
                }
                textColor = if (enabled) M3Colors.onPrimary else M3Colors.withAlpha(M3Colors.onSurface, 95)
            }
            M3ButtonStyle.FILLED_TONAL -> {
                backgroundColor = if (!enabled) {
                    M3Colors.withAlpha(M3Colors.onSurface, 30)
                } else {
                    applyStateLayer(M3Colors.secondaryContainer, hoverProgress, pressProgress)
                }
                textColor = if (enabled) M3Colors.onSecondaryContainer else M3Colors.withAlpha(M3Colors.onSurface, 95)
            }
            M3ButtonStyle.TEXT -> {
                backgroundColor = if (isHovered || isPressed) {
                    M3Colors.withAlpha(M3Colors.primary, ((hoverProgress * 0.08f + pressProgress * 0.12f) * 255).toInt())
                } else {
                    Color(0, 0, 0, 0)
                }
                textColor = if (enabled) M3Colors.primary else M3Colors.withAlpha(M3Colors.onSurface, 95)
            }
        }

        RenderUtils.drawRoundedRect(
            x, y,
            x + width, y + height,
            cornerRadius,
            backgroundColor.rgb
        )

        val font = Fonts.font35
        font.drawCenteredString(
            text,
            x + width / 2f,
            y + height / 2f - font.FONT_HEIGHT / 2f + 1,
            textColor.rgb,
            false
        )
    }

    private fun applyStateLayer(base: Color, hover: Float, press: Float): Color {
        val hoverOverlay = 0.08f * hover
        val pressOverlay = 0.12f * press
        val totalOverlay = (hoverOverlay + pressOverlay).coerceAtMost(0.16f)
        
        return Color(
            (base.red * (1 - totalOverlay)).toInt().coerceIn(0, 255),
            (base.green * (1 - totalOverlay)).toInt().coerceIn(0, 255),
            (base.blue * (1 - totalOverlay)).toInt().coerceIn(0, 255),
            base.alpha
        )
    }

    fun isHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height
    }

    fun mousePressed(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        if (mouseButton == 0 && isHovered(mouseX, mouseY) && enabled) {
            isPressed = true
            return true
        }
        return false
    }

    fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        val wasPressed = isPressed
        isPressed = false
        return wasPressed && isHovered(mouseX, mouseY) && enabled
    }
}
