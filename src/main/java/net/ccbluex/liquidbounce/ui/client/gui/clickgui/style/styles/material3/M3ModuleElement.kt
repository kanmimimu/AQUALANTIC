package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.value.*
import net.ccbluex.liquidbounce.ui.client.gui.ClickGUIModule
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.BlendUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

class M3ModuleElement(val module: Module) : MinecraftInstance() {
    
    companion object {
        private val expandIcon = ResourceLocation("CrossSine/ui/clickgui/new/expand.png")
    }
    
    private val toggleSwitch = M3ToggleSwitch()
    private val valueElements = mutableListOf<M3ValueElement<*>>()
    
    var animHeight = 0f
    private var fadeKeybind = 0f
    private var animPercent = 0f
    
    private var listeningToKey = false
    var expanded = false
    
    init {
        for (value in module.values) {
            when (value) {
                is BoolValue -> valueElements.add(M3BoolElement(value))
                is ListValue -> valueElements.add(M3ListElement(value))
                is IntegerValue -> valueElements.add(M3IntElement(value))
                is FloatValue -> valueElements.add(M3FloatElement(value))
                is TextValue -> valueElements.add(M3TextElement(value))
                is TitleValue -> valueElements.add(M3TitleElement(value))
            }
        }
    }
    
    fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float, accentColor: Color): Float {
        animPercent = animSmooth(animPercent, if (expanded) 100f else 0f, 0.5f)
        
        var expectedHeight = 0f
        for (ve in valueElements) {
            if (ve.isDisplayable()) {
                expectedHeight += ve.valueHeight
            }
        }
        animHeight = animPercent / 100f * (expectedHeight + 10f)
        
        Stencil.write(true)
        RenderUtils.drawRoundedRect(
            x + 10f, y + 5f, 
            x + width - 10f, y + height + animHeight - 5f, 
            M3Dimensions.cornerMedium, 
            M3Colors.surfaceContainerHigh.rgb
        )
        Stencil.erase(true)
        
        if (expanded || animHeight > 0f) {
            RenderUtils.newDrawRect(x + 10f, y + height - 5f, x + width - 10f, y + height - 4.5f, M3Colors.outlineVariant.rgb)
        }
        
        Fonts.SFApple40.drawString(
            module.name, 
            x + 18.5f, 
            y + 4f + height / 2f - Fonts.SFApple40.FONT_HEIGHT + 3f, 
            M3Colors.onSurface.rgb
        )
        
        val keyName = if (listeningToKey) "..." else Keyboard.getKeyName(module.keyBind)
        val keybindX = x + 25f + Fonts.SFApple40.getStringWidth(module.name)
        val keybindY = y + height / 2f - Fonts.SFApple40.FONT_HEIGHT + 6.5f
        val keybindWidth = Fonts.SFApple24.getStringWidth(keyName) + 10f
        
        val keybindHovered = MouseUtils.mouseWithinBounds(
            mouseX, mouseY,
            keybindX, keybindY,
            keybindX + keybindWidth, y + 4.5f + height / 2f
        )
        
        fadeKeybind = if (keybindHovered) {
            (fadeKeybind + 0.1f * RenderUtils.deltaTime * 0.025f).coerceIn(0f, 1f)
        } else {
            (fadeKeybind - 0.1f * RenderUtils.deltaTime * 0.025f).coerceIn(0f, 1f)
        }
        
        val keybindBgColor = BlendUtils.blend(
            M3Colors.surfaceContainerHighest,
            M3Colors.secondaryContainer,
            fadeKeybind.toDouble()
        )
        
        RenderUtils.drawRoundedRect(
            keybindX, keybindY,
            keybindX + keybindWidth, y + 4.5f + height / 2f,
            M3Dimensions.cornerSmall, keybindBgColor.rgb
        )
        
        Fonts.SFApple24.drawString(
            keyName,
            keybindX + 5f, y + height / 2f - Fonts.SFApple40.FONT_HEIGHT + 10f,
            M3Colors.onSurfaceVariant.rgb
        )
        
        toggleSwitch.state = module.state
        
        if (module.values.isNotEmpty()) {
            RenderUtils.newDrawRect(
                x + width - 40f, y + 5f, 
                x + width - 39.5f, y + height - 5f, 
                M3Colors.outlineVariant.rgb
            )
            
            GlStateManager.resetColor()
            GL11.glPushMatrix()
            GL11.glTranslatef(x + width - 25f, y + height / 2f, 0f)
            GL11.glPushMatrix()
            GL11.glRotatef(180f * (animHeight / (expectedHeight + 10f)), 0f, 0f, 1f)
            GL11.glColor4f(1f, 1f, 1f, 1f)
            RenderUtils.drawImage(expandIcon, -4, -4, 8, 8)
            GL11.glPopMatrix()
            GL11.glPopMatrix()
            
            toggleSwitch.onDraw(x + width - 70f, y + height / 2f - 5f, 20f, 10f, accentColor)
        } else {
            toggleSwitch.onDraw(x + width - 40f, y + height / 2f - 5f, 20f, 10f, accentColor)
        }
        
        if (expanded || animHeight > 0f) {
            var startYPos = y + height
            for (ve in valueElements) {
                if (ve.isDisplayable()) {
                    startYPos += ve.drawElement(
                        mouseX, mouseY, 
                        x + 10f, startYPos, 
                        width - 20f, 
                        M3Colors.surfaceContainerHigh, 
                        accentColor
                    )
                }
            }
        }
        
        Stencil.dispose()
        
        return height + animHeight
    }
    
    fun handleClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float) {
        if (listeningToKey) {
            resetState()
            return
        }
        
        val keyName = Keyboard.getKeyName(module.keyBind)
        val keybindX = x + 25f + Fonts.SFApple40.getStringWidth(module.name)
        val keybindY = y + height / 2f - Fonts.SFApple40.FONT_HEIGHT + 2f
        val keybindWidth = Fonts.SFApple24.getStringWidth(keyName) + 10f
        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, keybindX, keybindY, keybindX + keybindWidth, y + height / 2f)) {
            listeningToKey = true
            MaterialDesign3Gui.getInstance().cant = true
            return
        }
        
        if (module.values.isNotEmpty()) {
            if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 70f, y, x + width - 50f, y + height)) {
                module.toggle()
            }
        } else {
            if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 40f, y, x + width - 20f, y + height)) {
                module.toggle()
            }
        }
        
        if (module.values.isNotEmpty() && MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 40f, y, x + width - 10f, y + height)) {
            expanded = !expanded
        }
        
        if (expanded) {
            var startY = y + height
            for (ve in valueElements) {
                if (!ve.isDisplayable()) continue
                ve.onClick(mouseX, mouseY, x + 10f, startY, width - 20f)
                startY += ve.valueHeight
            }
        }
    }
    
    fun handleRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float) {
        if (expanded) {
            var startY = y + height
            for (ve in valueElements) {
                if (!ve.isDisplayable()) continue
                ve.onRelease(mouseX, mouseY, x + 10f, startY, width - 20f)
                startY += ve.valueHeight
            }
        }
    }
    
    fun handleKeyTyped(typed: Char, code: Int): Boolean {
        if (listeningToKey) {
            if (code == Keyboard.KEY_ESCAPE) {
                module.keyBind = 0
            } else {
                module.keyBind = code
            }
            listeningToKey = false
            MaterialDesign3Gui.getInstance().cant = false
            return true
        }
        
        if (expanded) {
            for (ve in valueElements) {
                if (ve.isDisplayable() && ve.onKeyPress(typed, code)) {
                    return true
                }
            }
        }
        return false
    }
    
    fun listeningKeybind() = listeningToKey
    
    fun resetState() {
        listeningToKey = false
        MaterialDesign3Gui.getInstance().cant = false
    }
    
    private fun animSmooth(current: Float, target: Float, speed: Float): Float {
        return if (ClickGUIModule.fastRenderValue.get()) {
            target
        } else {
            AnimationUtils.animate(target, current, speed * RenderUtils.deltaTime * 0.025f)
        }
    }
}

class M3ToggleSwitch {
    private var smooth = 0f
    private var thumbScale = 0f
    var state = false
    
    fun onDraw(x: Float, y: Float, width: Float, height: Float, accentColor: Color) {
        smooth = if (ClickGUIModule.fastRenderValue.get()) {
            if (state) 1f else 0f
        } else {
            smooth + (if (state) 0.15f else -0.15f) * RenderUtils.deltaTime * 0.045f
        }.coerceIn(0f, 1f)
        
        thumbScale = if (ClickGUIModule.fastRenderValue.get()) {
            if (state) 1f else 0f
        } else {
            thumbScale + (if (state) 0.12f else -0.12f) * RenderUtils.deltaTime * 0.045f
        }.coerceIn(0f, 1f)
        
        // Track colors (M3 Light theme)
        val trackColor = BlendUtils.blendColors(
            floatArrayOf(0f, 1f),
            arrayOf(M3Colors.surfaceContainerHighest, M3Colors.primary),
            smooth
        )
        
        // Outline (only visible when OFF)
        val outlineAlpha = ((1f - smooth) * 255).toInt().coerceIn(0, 255)
        if (outlineAlpha > 0) {
            RenderUtils.drawRoundedRect(
                x - 1f, y - 1f, 
                x + width + 1f, y + height + 1f, 
                (height + 2f) / 2f, 
                M3Colors.withAlpha(M3Colors.outline, outlineAlpha).rgb
            )
        }
        
        // Track fill
        RenderUtils.drawRoundedRect(
            x, y, x + width, y + height, 
            height / 2f, 
            trackColor.rgb
        )
        
        // Thumb color: OFF=outline, ON=onPrimary
        val thumbColor = BlendUtils.blendColors(
            floatArrayOf(0f, 1f),
            arrayOf(M3Colors.outline, M3Colors.onPrimary),
            smooth
        )
        
        // Thumb size grows when ON (M3 spec: 16dp OFF, 24dp ON)
        val minThumbRadius = (height - 6f) / 2f * 0.7f
        val maxThumbRadius = (height - 4f) / 2f
        val thumbRadius = minThumbRadius + (maxThumbRadius - minThumbRadius) * thumbScale
        
        val thumbX = x + (1f - smooth) * (3f + minThumbRadius) + smooth * (width - 3f - maxThumbRadius)
        val thumbY = y + height / 2f
        
        RenderUtils.drawFilledCircle(thumbX, thumbY, thumbRadius, thumbColor)
    }
}
