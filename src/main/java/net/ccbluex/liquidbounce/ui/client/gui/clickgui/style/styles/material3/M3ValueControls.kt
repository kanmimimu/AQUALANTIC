package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.features.value.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.input.Keyboard
import java.awt.Color

/**
 * Base class for M3 value controls
 */
sealed class M3ValueControl(val value: Value<*>) {
    abstract fun getHeight(): Float
    abstract fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float)
    open fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {}
    open fun mouseReleased() {}
    open fun keyTyped(typedChar: Char, keyCode: Int): Boolean = false
    
    companion object {
        fun create(value: Value<*>): M3ValueControl {
            return when (value) {
                is BoolValue -> M3SwitchControl(value)
                is FloatValue -> M3SliderControl(value)
                is IntegerValue -> M3IntSliderControl(value)
                is ListValue -> M3DropdownControl(value)
                is TextValue -> M3TextFieldControl(value)
                else -> M3UnknownControl(value)
            }
        }
    }
}

/**
 * M3 Switch Control for BoolValue
 */
class M3SwitchControl(private val boolValue: BoolValue) : M3ValueControl(boolValue) {
    private var toggleProgress = 0f
    
    override fun getHeight() = 40f
    
    override fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float) {
        if (alpha < 0.01f) return
        
        val targetToggle = if (boolValue.get()) 1f else 0f
        toggleProgress += (targetToggle - toggleProgress) * 0.2f
        
        // Label
        val textAlpha = (255 * alpha).toInt()
        Fonts.SFApple35.drawString(
            boolValue.name,
            x, y + getHeight() / 2 - 5,
            M3Colors.withAlpha(M3Colors.onSurfaceVariant, textAlpha).rgb
        )
        
        // Switch
        val switchX = x + width - M3Dimensions.switchWidth
        val switchY = y + (getHeight() - M3Dimensions.switchHeight) / 2
        drawMiniSwitch(switchX, switchY, alpha)
    }
    
    private fun drawMiniSwitch(x: Float, y: Float, alpha: Float) {
        val w = M3Dimensions.switchWidth * 0.8f
        val h = M3Dimensions.switchHeight * 0.8f
        val cornerRadius = h / 2  // Pill shape
        
        val trackColor = interpolateColor(M3Colors.surfaceContainerHighest, M3Colors.primary, toggleProgress)
        RenderUtils.drawRoundedRect(x, y, x + w, y + h, cornerRadius, 
            M3Colors.withAlpha(trackColor, (255 * alpha).toInt()).rgb)
        
        val thumbPadding = 2f
        val thumbSize = h - thumbPadding * 2
        val thumbX = x + thumbPadding + (w - thumbPadding * 2 - thumbSize) * toggleProgress
        val thumbY = y + thumbPadding
        val thumbColor = if (toggleProgress > 0.5f) M3Colors.onPrimary else M3Colors.onSurfaceVariant
        
        RenderUtils.drawRoundedRect(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize,
            thumbSize / 2, M3Colors.withAlpha(thumbColor, (255 * alpha).toInt()).rgb)
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {
        if (mouseButton == 0) {
            boolValue.set(!boolValue.get())
        }
    }
    
    private fun interpolateColor(c1: Color, c2: Color, f: Float): Color {
        val factor = f.coerceIn(0f, 1f)
        return Color(
            (c1.red + (c2.red - c1.red) * factor).toInt(),
            (c1.green + (c2.green - c1.green) * factor).toInt(),
            (c1.blue + (c2.blue - c1.blue) * factor).toInt()
        )
    }
}

/**
 * M3 Slider Control for FloatValue
 */
class M3SliderControl(private val floatValue: FloatValue) : M3ValueControl(floatValue) {
    private var dragging = false
    private var displayValue = floatValue.get()
    
    override fun getHeight() = 48f
    
    override fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float) {
        if (alpha < 0.01f) return
        
        displayValue += (floatValue.get() - displayValue) * 0.3f
        
        val textAlpha = (255 * alpha).toInt()
        
        // Label and value
        Fonts.SFApple35.drawString(
            floatValue.name,
            x, y + 4,
            M3Colors.withAlpha(M3Colors.onSurfaceVariant, textAlpha).rgb
        )
        
        val valueStr = String.format("%.2f", displayValue)
        val valueWidth = Fonts.SFApple35.getStringWidth(valueStr)
        Fonts.SFApple35.drawString(
            valueStr,
            x + width - valueWidth, y + 4,
            M3Colors.withAlpha(M3Colors.primary, textAlpha).rgb
        )
        
        // Slider track
        val trackY = y + 28
        val trackHeight = M3Dimensions.sliderTrackHeight
        val trackRadius = trackHeight / 2  // Pill shape
        val range = floatValue.maximum - floatValue.minimum
        val progress = if (range > 0) (displayValue - floatValue.minimum) / range else 0f
        
        // Inactive track
        RenderUtils.drawRoundedRect(
            x, trackY, x + width, trackY + trackHeight,
            trackRadius,
            M3Colors.withAlpha(M3Colors.surfaceContainerHighest, textAlpha).rgb
        )
        
        // Active track
        val activeWidth = width * progress.coerceIn(0f, 1f)
        if (activeWidth > 4) {
            RenderUtils.drawRoundedRect(
                x, trackY, x + activeWidth, trackY + trackHeight,
                trackRadius,
                M3Colors.withAlpha(M3Colors.primary, textAlpha).rgb
            )
        }
        
        // Thumb
        val thumbSize = M3Dimensions.sliderThumbSize
        val thumbRadius = thumbSize / 2
        val thumbX = x + activeWidth - thumbSize / 2
        val thumbY = trackY + trackHeight / 2 - thumbSize / 2
        
        RenderUtils.drawRoundedRect(
            thumbX.coerceIn(x - thumbSize / 2, x + width - thumbSize / 2), thumbY,
            (thumbX + thumbSize).coerceIn(x + thumbSize / 2, x + width + thumbSize / 2), thumbY + thumbSize,
            thumbRadius,
            M3Colors.withAlpha(M3Colors.primary, textAlpha).rgb
        )
        
        // Handle dragging
        if (dragging) {
            val newProgress = ((mouseX - x) / width).coerceIn(0f, 1f)
            val newValue = floatValue.minimum + newProgress * range
            floatValue.set(newValue)
        }
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {
        if (mouseButton == 0 && mouseY >= y + 20) {
            dragging = true
            val range = floatValue.maximum - floatValue.minimum
            val newProgress = ((mouseX - x) / width).coerceIn(0f, 1f)
            floatValue.set(floatValue.minimum + newProgress * range)
        }
    }
    
    override fun mouseReleased() {
        dragging = false
    }
}

/**
 * M3 Slider Control for IntegerValue
 */
class M3IntSliderControl(private val intValue: IntegerValue) : M3ValueControl(intValue) {
    private var dragging = false
    private var displayValue = intValue.get().toFloat()
    
    override fun getHeight() = 48f
    
    override fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float) {
        if (alpha < 0.01f) return
        
        displayValue += (intValue.get() - displayValue) * 0.3f
        
        val textAlpha = (255 * alpha).toInt()
        
        // Label and value
        Fonts.SFApple35.drawString(
            intValue.name,
            x, y + 4,
            M3Colors.withAlpha(M3Colors.onSurfaceVariant, textAlpha).rgb
        )
        
        val valueStr = intValue.get().toString()
        val valueWidth = Fonts.SFApple35.getStringWidth(valueStr)
        Fonts.SFApple35.drawString(
            valueStr,
            x + width - valueWidth, y + 4,
            M3Colors.withAlpha(M3Colors.primary, textAlpha).rgb
        )
        
        // Slider track
        val trackY = y + 28
        val trackHeight = M3Dimensions.sliderTrackHeight
        val trackRadius = trackHeight / 2  // Pill shape
        val range = (intValue.maximum - intValue.minimum).toFloat()
        val progress = if (range > 0) (displayValue - intValue.minimum) / range else 0f
        
        // Inactive track
        RenderUtils.drawRoundedRect(
            x, trackY, x + width, trackY + trackHeight,
            trackRadius,
            M3Colors.withAlpha(M3Colors.surfaceContainerHighest, textAlpha).rgb
        )
        
        // Active track
        val activeWidth = width * progress.coerceIn(0f, 1f)
        if (activeWidth > 4) {
            RenderUtils.drawRoundedRect(
                x, trackY, x + activeWidth, trackY + trackHeight,
                trackRadius,
                M3Colors.withAlpha(M3Colors.primary, textAlpha).rgb
            )
        }
        
        // Thumb
        val thumbSize = M3Dimensions.sliderThumbSize
        val thumbRadius = thumbSize / 2
        val thumbX = x + activeWidth - thumbSize / 2
        val thumbY = trackY + trackHeight / 2 - thumbSize / 2
        
        RenderUtils.drawRoundedRect(
            thumbX.coerceIn(x - thumbSize / 2, x + width - thumbSize / 2), thumbY,
            (thumbX + thumbSize).coerceIn(x + thumbSize / 2, x + width + thumbSize / 2), thumbY + thumbSize,
            thumbRadius,
            M3Colors.withAlpha(M3Colors.primary, textAlpha).rgb
        )
        
        // Handle dragging
        if (dragging) {
            val newProgress = ((mouseX - x) / width).coerceIn(0f, 1f)
            val newValue = (intValue.minimum + newProgress * range).toInt()
            intValue.set(newValue)
        }
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {
        if (mouseButton == 0 && mouseY >= y + 20) {
            dragging = true
            val range = (intValue.maximum - intValue.minimum).toFloat()
            val newProgress = ((mouseX - x) / width).coerceIn(0f, 1f)
            intValue.set((intValue.minimum + newProgress * range).toInt())
        }
    }
    
    override fun mouseReleased() {
        dragging = false
    }
}

/**
 * M3 Dropdown Control for ListValue
 */
class M3DropdownControl(private val listValue: ListValue) : M3ValueControl(listValue) {
    private var expanded = false
    private var expandProgress = 0f
    
    override fun getHeight(): Float {
        val baseHeight = 40f
        return if (expanded) baseHeight + listValue.values.size * 28f * expandProgress else baseHeight
    }
    
    override fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float) {
        if (alpha < 0.01f) return
        
        val targetProgress = if (expanded) 1f else 0f
        expandProgress += (targetProgress - expandProgress) * 0.15f
        
        val textAlpha = (255 * alpha).toInt()
        
        // Label
        Fonts.SFApple35.drawString(
            listValue.name,
            x, y + 12,
            M3Colors.withAlpha(M3Colors.onSurfaceVariant, textAlpha).rgb
        )
        
        // Current value chip
        val currentValue = listValue.get()
        val chipWidth = Fonts.SFApple35.getStringWidth(currentValue) + 24f
        val chipX = x + width - chipWidth
        val chipY = y + 6
        val chipHeight = 28f
        
        RenderUtils.drawRoundedRect(
            chipX, chipY, chipX + chipWidth, chipY + chipHeight,
            M3Dimensions.cornerSmall,
            M3Colors.withAlpha(M3Colors.secondaryContainer, textAlpha).rgb
        )
        
        Fonts.SFApple35.drawString(
            currentValue,
            chipX + 12, chipY + 8,
            M3Colors.withAlpha(M3Colors.onSecondaryContainer, textAlpha).rgb
        )
        
        // Dropdown arrow
        val arrow = if (expanded) "▲" else "▼"
        Fonts.SFApple35.drawString(arrow, chipX + chipWidth - 18, chipY + 8,
            M3Colors.withAlpha(M3Colors.onSecondaryContainer, textAlpha).rgb)
        
        // Dropdown options
        if (expandProgress > 0.01f) {
            var optionY = y + 40f
            for (option in listValue.values) {
                val isSelected = option == currentValue
                val optionHeight = 28f * expandProgress
                
                if (isSelected) {
                    RenderUtils.drawRoundedRect(
                        x, optionY, x + width, optionY + optionHeight,
                        M3Dimensions.cornerSmall,
                        M3Colors.withAlpha(M3Colors.primaryContainer, (200 * alpha * expandProgress).toInt()).rgb
                    )
                }
                
                val optionColor = if (isSelected) M3Colors.onPrimaryContainer else M3Colors.onSurfaceVariant
                Fonts.SFApple35.drawString(
                    option,
                    x + 12, optionY + 8 * expandProgress,
                    M3Colors.withAlpha(optionColor, (255 * alpha * expandProgress).toInt()).rgb
                )
                
                optionY += optionHeight
            }
        }
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {
        if (mouseButton == 0) {
            if (mouseY <= y + 40) {
                expanded = !expanded
            } else if (expanded) {
                var optionY = y + 40f
                for (option in listValue.values) {
                    if (mouseY >= optionY && mouseY <= optionY + 28) {
                        listValue.set(option)
                        expanded = false
                        return
                    }
                    optionY += 28f
                }
            }
        }
    }
}

/**
 * M3 TextField Control for TextValue
 */
class M3TextFieldControl(private val textValue: TextValue) : M3ValueControl(textValue) {
    private var focused = false
    private var text = textValue.get()
    private var cursorBlink = 0
    
    override fun getHeight() = 56f
    
    override fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float) {
        if (alpha < 0.01f) return
        
        cursorBlink++
        val textAlpha = (255 * alpha).toInt()
        
        // Label
        Fonts.SFApple35.drawString(
            textValue.name,
            x, y + 4,
            M3Colors.withAlpha(M3Colors.onSurfaceVariant, textAlpha).rgb
        )
        
        // Text field background
        val fieldY = y + 24
        val fieldHeight = 28f
        val borderColor = if (focused) M3Colors.primary else M3Colors.outline
        
        RenderUtils.drawRoundedRect(
            x, fieldY, x + width, fieldY + fieldHeight,
            M3Dimensions.cornerSmall,
            M3Colors.withAlpha(M3Colors.surfaceContainerHigh, textAlpha).rgb
        )
        // Draw border using outline color
        RenderUtils.drawBorderedRect(
            x.toDouble(), fieldY.toDouble(), 
            (x + width).toDouble(), (fieldY + fieldHeight).toDouble(), 
            1.0,
            M3Colors.withAlpha(borderColor, textAlpha).rgb,
            0
        )
        
        // Text
        val displayText = if (focused) text + (if (cursorBlink % 80 < 40) "|" else "") else text
        Fonts.SFApple35.drawString(
            displayText,
            x + 8, fieldY + 8,
            M3Colors.withAlpha(M3Colors.onSurface, textAlpha).rgb
        )
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float) {
        focused = mouseY >= y + 24 && mouseY <= y + 52
    }
    
    override fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        if (!focused) return false
        
        when (keyCode) {
            Keyboard.KEY_BACK -> {
                if (text.isNotEmpty()) {
                    text = text.dropLast(1)
                    textValue.set(text)
                }
            }
            Keyboard.KEY_RETURN, Keyboard.KEY_ESCAPE -> {
                focused = false
            }
            else -> {
                if (typedChar.isLetterOrDigit() || typedChar == ' ' || typedChar == '_' || typedChar == '-') {
                    text += typedChar
                    textValue.set(text)
                }
            }
        }
        return true
    }
}

/**
 * Fallback for unknown value types
 */
class M3UnknownControl(value: Value<*>) : M3ValueControl(value) {
    override fun getHeight() = 24f
    
    override fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, alpha: Float) {
        Fonts.SFApple35.drawString(
            "${value.name}: ${value.get()}",
            x, y + 6,
            M3Colors.withAlpha(M3Colors.onSurfaceVariant, (255 * alpha).toInt()).rgb
        )
    }
}
