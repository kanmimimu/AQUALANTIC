package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.features.value.*
import net.ccbluex.liquidbounce.ui.client.gui.ClickGUIModule
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.input.Keyboard
import java.awt.Color

abstract class M3ValueElement<T>(val value: Value<T>) {
    abstract val valueHeight: Float
    
    open fun isDisplayable(): Boolean = value.displayable
    
    abstract fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float
    
    open fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {}
    open fun onRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {}
    open fun onKeyPress(typed: Char, code: Int): Boolean = false
    open fun onTyped(typed: Char, code: Int) {}
    
    protected fun animSmooth(current: Float, target: Float, speed: Float): Float {
        return if (ClickGUIModule.fastRenderValue.get()) {
            target
        } else {
            AnimationUtils.animate(target, current, speed * RenderUtils.deltaTime * 0.025f)
        }
    }
}

class M3BoolElement(private val boolValue: BoolValue) : M3ValueElement<Boolean>(boolValue) {
    override val valueHeight = 28f
    private var smooth = 0f
    
    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        smooth = animSmooth(smooth, if (boolValue.get()) 1f else 0f, 0.5f)
        
        Fonts.SFApple35.drawString(
            boolValue.name,
            x + 8f, y + valueHeight / 2f - 4f,
            M3Colors.onSurfaceVariant.rgb
        )
        
        val switchX = x + width - 28f
        val switchY = y + valueHeight / 2f - 5f
        val switchWidth = 20f
        val switchHeight = 10f
        
        val trackColor = BlendUtils.blendColors(
            floatArrayOf(0f, 1f),
            arrayOf(M3Colors.surfaceContainerHighest, accentColor),
            smooth
        )
        
        RenderUtils.drawRoundedRect(
            switchX, switchY, 
            switchX + switchWidth, switchY + switchHeight, 
            switchHeight / 2f, 
            trackColor.rgb
        )
        
        val thumbRadius = (switchHeight - 4f) / 2f
        val thumbX = switchX + (1f - smooth) * (2f + thumbRadius) + smooth * (switchWidth - 2f - thumbRadius)
        val thumbColor = BlendUtils.blendColors(
            floatArrayOf(0f, 1f),
            arrayOf(M3Colors.onSurfaceVariant, bgColor),
            smooth
        )
        
        RenderUtils.drawFilledCircle(thumbX, switchY + 2f + thumbRadius, thumbRadius, thumbColor)
        
        return valueHeight
    }
    
    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + valueHeight)) {
            boolValue.set(!boolValue.get())
        }
    }
}

class M3ListElement(private val listValue: ListValue) : M3ValueElement<String>(listValue) {
    override val valueHeight = 28f
    private var expanded = false
    private var expandAnim = 0f
    
    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        expandAnim = animSmooth(expandAnim, if (expanded) 1f else 0f, 0.4f)
        
        val currentValue = listValue.get()
        val valueWidth = Fonts.SFApple35.getStringWidth(currentValue) + 20f
        
        Fonts.SFApple35.drawString(
            listValue.name,
            x + 8f, y + valueHeight / 2f - 4f,
            M3Colors.onSurfaceVariant.rgb
        )
        
        val chipX = x + width - valueWidth - 8f
        val chipY = y + 4f
        val chipHeight = valueHeight - 8f
        
        RenderUtils.drawRoundedRect(
            chipX, chipY, 
            chipX + valueWidth, chipY + chipHeight,
            M3Dimensions.cornerSmall,
            M3Colors.secondaryContainer.rgb
        )
        
        Fonts.SFApple35.drawString(
            currentValue,
            chipX + 8f, chipY + chipHeight / 2f - 4f,
            M3Colors.onSecondaryContainer.rgb
        )
        
        val arrow = if (expanded) "▲" else "▼"
        Fonts.SFApple24.drawString(arrow, chipX + valueWidth - 14f, chipY + chipHeight / 2f - 3f, M3Colors.onSecondaryContainer.rgb)
        
        if (expandAnim > 0.01f) {
            var optionY = y + valueHeight
            for (option in listValue.values) {
                if (option == currentValue) continue
                
                val optionHeight = 22f * expandAnim
                val isHovered = MouseUtils.mouseWithinBounds(mouseX, mouseY, chipX, optionY, chipX + valueWidth, optionY + optionHeight)
                
                if (isHovered) {
                    RenderUtils.drawRoundedRect(
                        chipX, optionY,
                        chipX + valueWidth, optionY + optionHeight,
                        M3Dimensions.cornerSmall,
                        M3Colors.withAlpha(M3Colors.primaryContainer, (180 * expandAnim).toInt()).rgb
                    )
                }
                
                Fonts.SFApple35.drawString(
                    option,
                    chipX + 8f, optionY + optionHeight / 2f - 4f * expandAnim,
                    M3Colors.withAlpha(M3Colors.onSurfaceVariant, (255 * expandAnim).toInt()).rgb
                )
                
                optionY += optionHeight
            }
        }
        
        return valueHeight + (listValue.values.size - 1) * 22f * expandAnim
    }
    
    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        val currentValue = listValue.get()
        val valueWidth = Fonts.SFApple35.getStringWidth(currentValue) + 20f
        val chipX = x + width - valueWidth - 8f
        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, chipX, y, chipX + valueWidth, y + valueHeight)) {
            expanded = !expanded
            return
        }
        
        if (expanded) {
            var optionY = y + valueHeight
            for (option in listValue.values) {
                if (option == currentValue) continue
                val optionHeight = 22f * expandAnim
                if (MouseUtils.mouseWithinBounds(mouseX, mouseY, chipX, optionY, chipX + valueWidth, optionY + optionHeight)) {
                    listValue.set(option)
                    expanded = false
                    return
                }
                optionY += optionHeight
            }
        }
    }
}

class M3IntElement(private val intValue: IntegerValue) : M3ValueElement<Int>(intValue) {
    override val valueHeight = 36f
    private var dragging = false
    private var displayValue = intValue.get().toFloat()
    
    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        displayValue = animSmooth(displayValue, intValue.get().toFloat(), 0.5f)
        
        Fonts.SFApple35.drawString(
            intValue.name,
            x + 8f, y + 4f,
            M3Colors.onSurfaceVariant.rgb
        )
        
        val valueStr = intValue.get().toString()
        val valueWidth = Fonts.SFApple35.getStringWidth(valueStr)
        Fonts.SFApple35.drawString(
            valueStr,
            x + width - valueWidth - 8f, y + 4f,
            accentColor.rgb
        )
        
        val sliderX = x + 8f
        val sliderY = y + 22f
        val sliderWidth = width - 16f
        val sliderHeight = 4f
        
        val range = (intValue.maximum - intValue.minimum).toFloat()
        val progress = if (range > 0) (displayValue - intValue.minimum) / range else 0f
        
        RenderUtils.drawRoundedRect(
            sliderX, sliderY,
            sliderX + sliderWidth, sliderY + sliderHeight,
            sliderHeight / 2f,
            M3Colors.surfaceContainerHighest.rgb
        )
        
        val activeWidth = sliderWidth * progress.coerceIn(0f, 1f)
        if (activeWidth > 2) {
            RenderUtils.drawRoundedRect(
                sliderX, sliderY,
                sliderX + activeWidth, sliderY + sliderHeight,
                sliderHeight / 2f,
                accentColor.rgb
            )
        }
        
        val thumbRadius = 5f
        val thumbX = sliderX + activeWidth
        RenderUtils.drawFilledCircle(
            thumbX.coerceIn(sliderX, sliderX + sliderWidth), 
            sliderY + sliderHeight / 2f, 
            thumbRadius, 
            accentColor
        )
        
        if (dragging) {
            val newProgress = ((mouseX - sliderX) / sliderWidth).coerceIn(0f, 1f)
            intValue.set((intValue.minimum + newProgress * range).toInt())
        }
        
        return valueHeight
    }
    
    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y + 18f, x + width, y + valueHeight)) {
            dragging = true
            val sliderX = x + 8f
            val sliderWidth = width - 16f
            val range = (intValue.maximum - intValue.minimum).toFloat()
            val newProgress = ((mouseX - sliderX) / sliderWidth).coerceIn(0f, 1f)
            intValue.set((intValue.minimum + newProgress * range).toInt())
        }
    }
    
    override fun onRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        dragging = false
    }
}

class M3FloatElement(private val floatValue: FloatValue) : M3ValueElement<Float>(floatValue) {
    override val valueHeight = 36f
    private var dragging = false
    private var displayValue = floatValue.get()
    
    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        displayValue = animSmooth(displayValue, floatValue.get(), 0.5f)
        
        Fonts.SFApple35.drawString(
            floatValue.name,
            x + 8f, y + 4f,
            M3Colors.onSurfaceVariant.rgb
        )
        
        val valueStr = String.format("%.2f", floatValue.get())
        val valueWidth = Fonts.SFApple35.getStringWidth(valueStr)
        Fonts.SFApple35.drawString(
            valueStr,
            x + width - valueWidth - 8f, y + 4f,
            accentColor.rgb
        )
        
        val sliderX = x + 8f
        val sliderY = y + 22f
        val sliderWidth = width - 16f
        val sliderHeight = 4f
        
        val range = floatValue.maximum - floatValue.minimum
        val progress = if (range > 0) (displayValue - floatValue.minimum) / range else 0f
        
        RenderUtils.drawRoundedRect(
            sliderX, sliderY,
            sliderX + sliderWidth, sliderY + sliderHeight,
            sliderHeight / 2f,
            M3Colors.surfaceContainerHighest.rgb
        )
        
        val activeWidth = sliderWidth * progress.coerceIn(0f, 1f)
        if (activeWidth > 2) {
            RenderUtils.drawRoundedRect(
                sliderX, sliderY,
                sliderX + activeWidth, sliderY + sliderHeight,
                sliderHeight / 2f,
                accentColor.rgb
            )
        }
        
        val thumbRadius = 5f
        val thumbX = sliderX + activeWidth
        RenderUtils.drawFilledCircle(
            thumbX.coerceIn(sliderX, sliderX + sliderWidth), 
            sliderY + sliderHeight / 2f, 
            thumbRadius, 
            accentColor
        )
        
        if (dragging) {
            val newProgress = ((mouseX - sliderX) / sliderWidth).coerceIn(0f, 1f)
            floatValue.set(floatValue.minimum + newProgress * range)
        }
        
        return valueHeight
    }
    
    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y + 18f, x + width, y + valueHeight)) {
            dragging = true
            val sliderX = x + 8f
            val sliderWidth = width - 16f
            val range = floatValue.maximum - floatValue.minimum
            val newProgress = ((mouseX - sliderX) / sliderWidth).coerceIn(0f, 1f)
            floatValue.set(floatValue.minimum + newProgress * range)
        }
    }
    
    override fun onRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        dragging = false
    }
}

class M3TextElement(private val textValue: TextValue) : M3ValueElement<String>(textValue) {
    override val valueHeight = 40f
    private var focused = false
    private var text = textValue.get()
    private var cursorBlink = 0
    
    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        cursorBlink++
        
        Fonts.SFApple35.drawString(
            textValue.name,
            x + 8f, y + 4f,
            M3Colors.onSurfaceVariant.rgb
        )
        
        val fieldX = x + 8f
        val fieldY = y + 20f
        val fieldWidth = width - 16f
        val fieldHeight = 16f
        
        val borderColor = if (focused) accentColor else M3Colors.outline
        
        RenderUtils.drawRoundedRect(
            fieldX, fieldY,
            fieldX + fieldWidth, fieldY + fieldHeight,
            M3Dimensions.cornerSmall,
            M3Colors.surfaceContainerHighest.rgb
        )
        
        val displayText = if (focused) text + (if (cursorBlink % 80 < 40) "|" else "") else text
        Fonts.SFApple35.drawString(
            displayText,
            fieldX + 4f, fieldY + 4f,
            M3Colors.onSurface.rgb
        )
        
        return valueHeight
    }
    
    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        focused = MouseUtils.mouseWithinBounds(mouseX, mouseY, x + 8f, y + 20f, x + width - 8f, y + 36f)
    }
    
    override fun onKeyPress(typed: Char, code: Int): Boolean {
        if (!focused) return false
        
        when (code) {
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
                if (typed.isLetterOrDigit() || typed == ' ' || typed == '_' || typed == '-') {
                    text += typed
                    textValue.set(text)
                }
            }
        }
        return true
    }
}

class M3TitleElement(private val titleValue: TitleValue) : M3ValueElement<String>(titleValue) {
    override val valueHeight = 20f
    
    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        RenderUtils.newDrawRect(x + 8f, y + valueHeight / 2f, x + width - 8f, y + valueHeight / 2f + 0.5f, M3Colors.outlineVariant.rgb)
        
        val textWidth = Fonts.SFApple35.getStringWidth(titleValue.name)
        val textX = x + (width - textWidth) / 2f
        
        RenderUtils.newDrawRect(textX - 4f, y, textX + textWidth + 4f, y + valueHeight, bgColor.rgb)
        
        Fonts.SFApple35.drawString(
            titleValue.name,
            textX, y + valueHeight / 2f - 4f,
            accentColor.rgb
        )
        
        return valueHeight
    }
}
