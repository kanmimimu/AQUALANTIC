package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.value.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.input.Keyboard
import java.awt.Color
import kotlin.math.max

/**
 * M3 Module Card - Displays a module with toggle switch and expandable settings
 */
class M3ModuleCard(val module: Module) {
    private var expanded = false
    private var expandProgress = 0f
    private var hoverProgress = 0f
    private var toggleProgress = 0f
    
    private val valueControls = module.values.map { M3ValueControl.create(it) }
    
    // Dragging state for sliders
    private var activeSlider: M3ValueControl? = null
    
    fun getHeight(): Float {
        val baseHeight = M3Dimensions.moduleCardHeight
        if (!expanded) return baseHeight
        
        var expandedHeight = M3Dimensions.moduleCardExpandedPadding
        valueControls.forEach { expandedHeight += it.getHeight() + M3Dimensions.spacingSm }
        return baseHeight + expandedHeight * expandProgress
    }
    
    fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        val baseHeight = M3Dimensions.moduleCardHeight
        val totalHeight = getHeight()
        val isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + totalHeight
        
        // Animate states
        val hoverTarget = if (isHovered) 1f else 0f
        hoverProgress += (hoverTarget - hoverProgress) * 0.15f
        
        val expandTarget = if (expanded) 1f else 0f
        expandProgress += (expandTarget - expandProgress) * 0.12f
        
        val toggleTarget = if (module.state) 1f else 0f
        toggleProgress += (toggleTarget - toggleProgress) * 0.2f
        
        // Draw card background
        val bgColor = if (hoverProgress > 0.01f) {
            M3Colors.hovered(M3Colors.surfaceContainerHigh)
        } else {
            M3Colors.surfaceContainerHigh
        }
        RenderUtils.drawRoundedRect(x, y, x + width, y + totalHeight, M3Dimensions.cornerMedium, bgColor.rgb)
        
        // Draw module name
        Fonts.SFApple40.drawString(
            module.name,
            x + M3Dimensions.spacingLg,
            y + baseHeight / 2 - 6,
            M3Colors.onSurface.rgb
        )
        
        // Draw switch
        val switchX = x + width - M3Dimensions.switchWidth - M3Dimensions.spacingLg
        val switchY = y + (baseHeight - M3Dimensions.switchHeight) / 2
        drawSwitch(switchX, switchY, mouseX, mouseY)
        
        // Draw expand indicator
        if (module.values.isNotEmpty()) {
            val arrowX = x + width - M3Dimensions.switchWidth - M3Dimensions.spacingLg - 24
            val arrowY = y + baseHeight / 2
            val arrowColor = M3Colors.onSurfaceVariant
            
            // Rotate arrow based on expand state
            val rotation = 90f * expandProgress
            drawExpandArrow(arrowX, arrowY, rotation, arrowColor)
        }
        
        // Draw expanded values
        if (expandProgress > 0.01f && valueControls.isNotEmpty()) {
            var valueY = y + baseHeight + M3Dimensions.moduleCardExpandedPadding * expandProgress
            for (control in valueControls) {
                control.draw(
                    mouseX, mouseY,
                    x + M3Dimensions.spacingLg,
                    valueY,
                    width - M3Dimensions.spacingLg * 2,
                    expandProgress
                )
                valueY += (control.getHeight() + M3Dimensions.spacingSm) * expandProgress
            }
        }
    }
    
    private fun drawSwitch(x: Float, y: Float, mouseX: Int, mouseY: Int) {
        val width = M3Dimensions.switchWidth
        val height = M3Dimensions.switchHeight
        val cornerRadius = height / 2  // Pill shape - use half height
        
        // Track background
        val trackColor = interpolateColor(M3Colors.surfaceContainerHighest, M3Colors.primary, toggleProgress)
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, cornerRadius, trackColor.rgb)
        
        // Thumb - consistent circle
        val thumbPadding = 3f
        val thumbSize = height - thumbPadding * 2  // Fixed size circle
        val thumbX = x + thumbPadding + (width - thumbPadding * 2 - thumbSize) * toggleProgress
        val thumbY = y + thumbPadding
        val thumbRadius = thumbSize / 2
        
        val thumbColor = if (toggleProgress > 0.5f) M3Colors.onPrimary else M3Colors.onSurfaceVariant
        RenderUtils.drawRoundedRect(
            thumbX, thumbY,
            thumbX + thumbSize, thumbY + thumbSize,
            thumbRadius,
            thumbColor.rgb
        )
    }
    
    private fun drawExpandArrow(x: Float, y: Float, rotation: Float, color: Color) {
        // Simple arrow using lines (chevron right that rotates to chevron down)
        val size = 4f
        Fonts.SFApple35.drawString(if (rotation > 45) "▼" else "▶", x - 3, y - 5, color.rgb)
    }
    
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {
        val baseHeight = M3Dimensions.moduleCardHeight
        
        // Check switch click
        val switchX = x + width - M3Dimensions.switchWidth - M3Dimensions.spacingLg
        val switchY = y + (baseHeight - M3Dimensions.switchHeight) / 2
        val switchWidth = M3Dimensions.switchWidth
        val switchHeight = M3Dimensions.switchHeight
        
        if (mouseButton == 0 && mouseX >= switchX && mouseX <= switchX + switchWidth &&
            mouseY >= switchY && mouseY <= switchY + switchHeight) {
            module.toggle()
            return
        }
        
        // Check header click
        if (mouseY <= y + baseHeight) {
            // Right-click to expand/collapse settings
            if (mouseButton == 1) {
                if (module.values.isNotEmpty()) {
                    expanded = !expanded
                }
            } else if (mouseButton == 0) {
                // Left-click to toggle module
                module.toggle()
            }
            return
        }
        
        // Check value control clicks
        if (expanded && expandProgress > 0.5f) {
            var valueY = y + baseHeight + M3Dimensions.moduleCardExpandedPadding * expandProgress
            for (control in valueControls) {
                val controlHeight = control.getHeight() * expandProgress
                if (mouseY >= valueY && mouseY <= valueY + controlHeight) {
                    control.mouseClicked(mouseX, mouseY, mouseButton, x + M3Dimensions.spacingLg, valueY, width - M3Dimensions.spacingLg * 2)
                    if (control is M3SliderControl) {
                        activeSlider = control
                    }
                    return
                }
                valueY += (control.getHeight() + M3Dimensions.spacingSm) * expandProgress
            }
        }
    }
    
    fun mouseReleased() {
        activeSlider = null
        valueControls.forEach { it.mouseReleased() }
    }
    
    fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        if (expanded) {
            valueControls.forEach { 
                if (it.keyTyped(typedChar, keyCode)) return true
            }
        }
        return false
    }
    
    private fun interpolateColor(c1: Color, c2: Color, factor: Float): Color {
        val f = factor.coerceIn(0f, 1f)
        return Color(
            (c1.red + (c2.red - c1.red) * f).toInt(),
            (c1.green + (c2.green - c1.green) * f).toInt(),
            (c1.blue + (c2.blue - c1.blue) * f).toInt(),
            (c1.alpha + (c2.alpha - c1.alpha) * f).toInt()
        )
    }
}
