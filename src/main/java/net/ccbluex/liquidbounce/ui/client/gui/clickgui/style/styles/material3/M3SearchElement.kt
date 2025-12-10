package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.ui.client.gui.ClickGUIModule
import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.newVer.IconManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs

class M3SearchElement(var xPos: Float, var yPos: Float, var width: Float, val height: Float) {
    
    private var scrollHeight = 0f
    private var animScrollHeight = 0f
    private var lastHeight = 0f
    private val startYY = 5f
    
    val searchBox = M3SearchBox(0, xPos.toInt() + 2, yPos.toInt() + 2, width.toInt() - 4, height.toInt() - 2)
    
    fun drawBox(mouseX: Int, mouseY: Int, accentColor: Color): Boolean {
        Stencil.write(true)
        RenderUtils.drawRoundedRect(
            xPos, yPos, 
            xPos + width, yPos + height, 
            M3Dimensions.cornerSmall, 
            M3Colors.surfaceContainerHigh.rgb
        )
        Stencil.erase(true)
        
        if (searchBox.isFocused) {
            RenderUtils.newDrawRect(xPos, yPos + height - 1f, xPos + width, yPos + height, accentColor.rgb)
            searchBox.drawTextBox()
        } else if (searchBox.text.isEmpty()) {
            Fonts.SFApple35.drawString("Search...", xPos + 4f, yPos + height / 2f - 4f, M3Colors.onSurfaceVariant.rgb)
        } else {
            searchBox.drawTextBox()
        }
        
        Stencil.dispose()
        GlStateManager.disableAlpha()
        RenderUtils.drawImage2(IconManager.search, xPos + width - 15f, yPos + 5f, 10, 10)
        GlStateManager.enableAlpha()
        
        return searchBox.text.isNotEmpty()
    }
    
    private fun searchMatch(module: M3ModuleElement): Boolean {
        return module.module.name.contains(searchBox.text, true)
    }
    
    private fun getSearchModules(ces: List<M3CategoryElement>): List<M3ModuleElement> {
        val modules = mutableListOf<M3ModuleElement>()
        ces.forEach { cat ->
            modules.addAll(cat.moduleElements.filter { searchMatch(it) })
        }
        return modules
    }
    
    fun drawPanel(mX: Int, mY: Int, x: Float, y: Float, w: Float, h: Float, wheel: Int, ces: List<M3CategoryElement>, accentColor: Color) {
        var mouseY = mY
        lastHeight = 0f
        
        getSearchModules(ces).forEach { mod ->
            if (searchMatch(mod)) {
                lastHeight += mod.animHeight + 40f
            }
        }
        
        if (lastHeight >= 10f) lastHeight -= 10f
        handleScrolling(wheel, h)
        drawScroll(x, y + startYY, w, h)
        
        Fonts.SFApple35.drawString(
            "Results", 
            x + 8f, y - 12f, 
            M3Colors.onSurfaceVariant.rgb
        )
        
        var startY = y + startYY
        if (mouseY < y + startYY || mouseY >= y + h) {
            mouseY = -1
        }
        
        RenderUtils.makeScissorBox(x, y + startYY, x + w, y + h)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        
        ces.forEach { cat ->
            cat.moduleElements.forEach { mod ->
                if (searchMatch(mod)) {
                    startY += if (startY + animScrollHeight > y + h || startY + animScrollHeight + 40f + mod.animHeight < y + startYY) {
                        40f + mod.animHeight
                    } else {
                        mod.drawElement(mX, mouseY, x, startY + animScrollHeight, w, 40f, accentColor)
                    }
                }
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
            val scrollBarY = 5f + scrollProgress * (height - (startYY + 10f) - scrollBarHeight)
            
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
    
    fun handleMouseClick(mX: Int, mY: Int, mouseButton: Int, x: Float, y: Float, w: Float, h: Float, ces: List<M3CategoryElement>) {
        if (MouseUtils.mouseWithinBounds(mX, mY, x - 200f, y - 20f, x - 170f, y)) {
            return
        }
        
        var mouseY = mY
        searchBox.mouseClicked(mX, mouseY, mouseButton)
        
        if (searchBox.text.isEmpty()) return
        
        if (mouseY < y + startYY || mouseY >= y + h) {
            mouseY = -1
        }
        
        var startY = y + startYY
        getSearchModules(ces).forEach { mod ->
            mod.handleClick(mX, mouseY, x, startY + animScrollHeight, w, 40f)
            startY += 40f + mod.animHeight
        }
    }
    
    fun handleMouseRelease(mX: Int, mY: Int, mouseButton: Int, x: Float, y: Float, w: Float, h: Float, ces: List<M3CategoryElement>) {
        if (searchBox.text.isEmpty()) return
        
        var mouseY = mY
        if (mouseY < y + startYY || mouseY >= y + h) {
            mouseY = -1
        }
        
        var startY = y + startYY
        getSearchModules(ces).forEach { mod ->
            mod.handleRelease(mX, mouseY, x, startY + animScrollHeight, w, 40f)
            startY += 40f + mod.animHeight
        }
    }
    
    fun handleTyping(typedChar: Char, keyCode: Int, x: Float, y: Float, w: Float, h: Float, ces: List<M3CategoryElement>): Boolean {
        searchBox.textboxKeyTyped(typedChar, keyCode)
        
        if (searchBox.text.isEmpty()) return false
        
        getSearchModules(ces).forEach { mod ->
            if (mod.handleKeyTyped(typedChar, keyCode)) {
                return true
            }
        }
        return false
    }
    
    fun isTyping(): Boolean = searchBox.text.isNotEmpty()
    
    private fun animSmooth(current: Float, target: Float, speed: Float): Float {
        return if (ClickGUIModule.fastRenderValue.get()) {
            target
        } else {
            AnimationUtils.animate(target, current, speed * RenderUtils.deltaTime * 0.025f)
        }
    }
}

class M3SearchBox(id: Int, x: Int, y: Int, width: Int, height: Int) : GuiTextField(id, Fonts.SFApple35, x, y, width, height) {
    init {
        maxStringLength = 50
        enableBackgroundDrawing = false
    }
}
