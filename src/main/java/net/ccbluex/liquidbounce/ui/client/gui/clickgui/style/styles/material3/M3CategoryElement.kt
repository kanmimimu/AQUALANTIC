package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.gui.ClickGUIModule
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class M3CategoryElement(val category: ModuleCategory) : MinecraftInstance() {
    val name = category.displayName
    var focused = false
    
    private var scrollHeight = 0f
    private var animScrollHeight = 0f
    private var lastHeight = 0f
    
    private val startYY = 5f
    
    val moduleElements = mutableListOf<M3ModuleElement>()
    
    init {
        CrossSine.moduleManager.modules
            .filter { it.category == category }
            .forEach { moduleElements.add(M3ModuleElement(it)) }
    }
    
    fun drawLabel(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float, accentColor: Color) {
        val isHovered = MouseUtils.mouseWithinBounds(mouseX, mouseY, x, y, x + width, y + height)
        
        if (focused) {
            RenderUtils.drawRoundedRect(
                x + 8f, y + 2f, 
                x + width - 4f, y + height - 2f, 
                M3Dimensions.cornerSmall, 
                M3Colors.secondaryContainer.rgb
            )
        } else if (isHovered) {
            RenderUtils.drawRoundedRect(
                x + 8f, y + 2f, 
                x + width - 4f, y + height - 2f, 
                M3Dimensions.cornerSmall, 
                M3Colors.surfaceContainerHigh.rgb
            )
        }
        
        val textColor = if (focused) M3Colors.onSecondaryContainer else M3Colors.onSurfaceVariant
        Fonts.SFApple40.drawString(
            name, 
            x + 14f, 
            y + height / 2f - Fonts.SFApple40.FONT_HEIGHT / 2f + 2f, 
            textColor.rgb
        )
    }
    
    fun drawPanel(mX: Int, mY: Int, x: Float, y: Float, width: Float, height: Float, wheel: Int, accentColor: Color) {
        var mouseX = mX
        var mouseY = mY
        
        lastHeight = 0f
        for (me in moduleElements) {
            lastHeight += 40f + me.animHeight
        }
        if (lastHeight >= 10f) lastHeight -= 10f
        
        handleScrolling(wheel, height)
        drawScroll(x, y + startYY, width, height)
        
        if (mouseY < y + startYY || mouseY >= y + height) {
            mouseY = -1
        }
        
        RenderUtils.makeScissorBox(x, y + startYY, x + width, y + height)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        
        var startY = y + startYY
        for (moduleElement in moduleElements) {
            if (startY + animScrollHeight > y + height || startY + animScrollHeight + 40f + moduleElement.animHeight < y + startYY) {
                startY += 40f + moduleElement.animHeight
            } else {
                startY += moduleElement.drawElement(mouseX, mouseY, x, startY + animScrollHeight, width, 40f, accentColor)
            }
        }
        
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }
    
    private fun handleScrolling(wheel: Int, height: Float) {
        if (wheel != 0) {
            scrollHeight += if (wheel > 0) 50f else -50f
        }
        
        if (lastHeight > height - (startYY + 10f)) {
            scrollHeight = scrollHeight.coerceIn(-lastHeight + height - (startYY + 10f), 0f)
        } else {
            scrollHeight = 0f
        }
        
        animScrollHeight = animSmooth(animScrollHeight, scrollHeight, 0.5f)
    }
    
    private fun drawScroll(x: Float, y: Float, width: Float, height: Float) {
        if (lastHeight > height - (startYY + 10f)) {
            val visibleRatio = (height - (startYY + 10f)) / lastHeight
            val scrollBarHeight = (height - (startYY + 10f)) * visibleRatio
            val scrollProgress = abs(animScrollHeight / (-lastHeight + height - (startYY + 10f))).coerceIn(0f, 1f)
            val scrollBarY = scrollProgress * (height - (startYY + 10f) - scrollBarHeight)
            
            RenderUtils.drawRoundedRect(
                x + width - 6f,
                y + scrollBarY,
                x + width - 4f,
                y + scrollBarY + scrollBarHeight,
                1f,
                M3Colors.withAlpha(M3Colors.onSurfaceVariant, 80).rgb
            )
        }
    }
    
    fun handleMouseClick(mX: Int, mY: Int, mouseButton: Int, x: Float, y: Float, width: Float, height: Float) {
        var mouseY = mY
        if (mouseY < y + startYY || mouseY >= y + height) {
            mouseY = -1
        }
        
        var startY = y + startYY
        if (mouseButton == 0) {
            for (moduleElement in moduleElements) {
                moduleElement.handleClick(mX, mouseY, x, startY + animScrollHeight, width, 40f)
                startY += 40f + moduleElement.animHeight
            }
        }
    }
    
    fun handleMouseRelease(mX: Int, mY: Int, mouseButton: Int, x: Float, y: Float, width: Float, height: Float) {
        var mouseY = mY
        if (mouseY < y + startYY || mouseY >= y + height) {
            mouseY = -1
        }
        
        var startY = y + startYY
        if (mouseButton == 0) {
            for (moduleElement in moduleElements) {
                moduleElement.handleRelease(mX, mouseY, x, startY + animScrollHeight, width, 40f)
                startY += 40f + moduleElement.animHeight
            }
        }
    }
    
    fun handleKeyTyped(keyTyped: Char, keyCode: Int): Boolean {
        for (moduleElement in moduleElements) {
            if (moduleElement.handleKeyTyped(keyTyped, keyCode)) {
                return true
            }
        }
        return false
    }
    
    private fun animSmooth(current: Float, target: Float, speed: Float): Float {
        return if (ClickGUIModule.fastRenderValue.get()) {
            target
        } else {
            AnimationUtils.animate(target, current, speed * RenderUtils.deltaTime * 0.025f)
        }
    }
}
