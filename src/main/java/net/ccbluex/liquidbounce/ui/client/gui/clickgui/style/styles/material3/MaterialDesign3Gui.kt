package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.gui.ClickGUIModule
import net.ccbluex.liquidbounce.ui.client.gui.GuiClientSettings
import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.newVer.IconManager
import net.ccbluex.liquidbounce.ui.client.gui.colortheme.ClientTheme
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.MouseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class MaterialDesign3Gui : GuiScreen() {
    
    private var windowXStart = 0f
    private var windowYStart = 0f
    private var windowXEnd = 0f
    private var windowYEnd = 0f
    
    private val windowWidth get() = windowXEnd - windowXStart
    private val windowHeight get() = windowYEnd - windowYStart
    
    private val margin = 40f
    private val maxWidth = 580f
    private val maxHeight = 420f
    private val cornerRadius = 20f
    
    private var sideWidth = 110f
    
    private var closed = false
    private var animProgress = 0f
    
    private var categorySelectAnim = 0f
    private var categorySelectTarget = 0f
    
    var cant = false
    
    override fun initGui() {
        super.initGui()
        Keyboard.enableRepeatEvents(true)
        
        val calcWidth = (width - margin * 2).coerceAtMost(maxWidth)
        val calcHeight = (height - margin * 2).coerceAtMost(maxHeight)
        windowXStart = (width - calcWidth) / 2
        windowYStart = (height - calcHeight) / 2
        windowXEnd = windowXStart + calcWidth
        windowYEnd = windowYStart + calcHeight
        
        searchElement.xPos = windowXStart + 8f
        searchElement.yPos = windowYStart + 32f
        searchElement.width = sideWidth - 16f
        
        categoryElements.forEach { cat ->
            cat.moduleElements.filter { it.listeningKeybind() }.forEach { it.resetState() }
        }
    }
    
    override fun onGuiClosed() {
        categoryElements.filter { it.focused }.forEach { 
            it.handleMouseRelease(-1, -1, 0, 0f, 0f, 0f, 0f) 
        }
        closed = false
        animProgress = 0f
        Keyboard.enableRepeatEvents(false)
        CrossSine.fileManager.saveConfigs()
    }
    
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val animSpeed = 0.04f
        animProgress = if (closed) {
            (animProgress - animSpeed).coerceAtLeast(0f)
        } else {
            (animProgress + animSpeed).coerceAtMost(1f)
        }
        
        if (closed && animProgress <= 0f) {
            mc.displayGuiScreen(null)
            return
        }
        
        val easeProgress = easeOutQuart(animProgress)
        val scale = 0.9f + 0.1f * easeProgress
        val alpha = easeProgress
        
        GL11.glPushMatrix()
        
        val centerX = (windowXStart + windowXEnd) / 2
        val centerY = (windowYStart + windowYEnd) / 2
        GL11.glTranslatef(centerX, centerY, 0f)
        GL11.glScalef(scale, scale, 1f)
        GL11.glTranslatef(-centerX, -centerY, 0f)
        
        drawWindow(mouseX, mouseY, alpha)
        
        GL11.glPopMatrix()
        
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
    
    private fun drawWindow(mouseX: Int, mouseY: Int, alpha: Float) {
        val accentColor = ClientTheme.getColor(1)
        val alphaInt = (255 * alpha).toInt()
        
        RenderUtils.drawRoundedRect(
            windowXStart, windowYStart,
            windowXEnd, windowYEnd,
            cornerRadius,
            M3Colors.withAlpha(M3Colors.surfaceDim, alphaInt).rgb
        )
        
        RenderUtils.customRounded(
            windowXStart, windowYStart,
            windowXStart + sideWidth, windowYEnd,
            cornerRadius, 0f, cornerRadius, 0f,
            M3Colors.withAlpha(M3Colors.surfaceContainerLow, alphaInt).rgb
        )
        
        GlStateManager.color(1f, 1f, 1f, alpha)
        GlStateManager.enableBlend()
        RenderUtils.drawImage(IconManager.brush, (windowXStart + 8f).toInt(), (windowYEnd - 36f).toInt(), 20, 20)
        RenderUtils.drawImage(IconManager.settings, (windowXStart + 8f).toInt(), (windowYEnd - 64f).toInt(), 20, 20)
        GlStateManager.disableBlend()
        
        searchElement.xPos = windowXStart + 8f
        searchElement.yPos = windowYStart + 8f
        searchElement.width = sideWidth - 16f
        
        if (searchElement.drawBox(mouseX, mouseY, accentColor)) {
            searchElement.drawPanel(
                mouseX, mouseY,
                windowXStart + sideWidth + 8f, windowYStart + 8f,
                windowWidth - sideWidth - 16f, windowHeight - 16f,
                Mouse.getDWheel(), categoryElements, accentColor
            )
            return
        }
        val elementsStartY = 36f
        val elementHeight = 28f
        var startY = windowYStart + elementsStartY
        
        categorySelectTarget = categoryElements.indexOfFirst { it.focused }.toFloat().coerceAtLeast(0f)
        categorySelectAnim = AnimationUtils.animate(categorySelectTarget, categorySelectAnim, 0.3f * RenderUtils.deltaTime * 0.025f)
        
        val indicatorY = windowYStart + elementsStartY + categorySelectAnim * elementHeight
        RenderUtils.drawRoundedRect(
            windowXStart + 4f, indicatorY + 2f,
            windowXStart + 8f, indicatorY + elementHeight - 2f,
            2f, accentColor.rgb
        )
        
        for ((index, ce) in categoryElements.withIndex()) {
            ce.drawLabel(mouseX, mouseY, windowXStart, startY, sideWidth, elementHeight, accentColor)
            if (ce.focused) {
                ce.drawPanel(
                    mouseX, mouseY,
                    windowXStart + sideWidth + 8f, windowYStart + 8f,
                    windowWidth - sideWidth - 16f, windowHeight - 16f,
                    Mouse.getDWheel(), accentColor
                )
            }
            startY += elementHeight
        }
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (searchElement.isTyping() && MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, windowYStart, windowXStart + 60f, windowYStart + 28f)) {
            searchElement.searchBox.text = ""
            return
        }
        

        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, windowYEnd - 40f, windowXStart + 36f, windowYEnd)) {
            mc.displayGuiScreen(GuiHudDesigner())
            return
        }
        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, windowYEnd - 68f, windowXStart + 36f, windowYEnd - 32f)) {
            mc.displayGuiScreen(GuiClientSettings(this))
            return
        }
        
        val elementsStartY = 36f
        val elementHeight = 28f
        var startY = windowYStart + elementsStartY
        
        searchElement.handleMouseClick(
            mouseX, mouseY, mouseButton,
            windowXStart + sideWidth + 8f, windowYStart + 8f,
            windowWidth - sideWidth - 16f, windowHeight - 16f,
            categoryElements
        )
        
        if (!searchElement.isTyping()) {
            categoryElements.forEach { cat ->
                if (cat.focused) {
                    cat.handleMouseClick(
                        mouseX, mouseY, mouseButton,
                        windowXStart + sideWidth + 8f, windowYStart + 8f,
                        windowWidth - sideWidth - 16f, windowHeight - 16f
                    )
                }
                
                if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, startY, windowXStart + sideWidth, startY + elementHeight) && !searchElement.isTyping()) {
                    categoryElements.forEach { it.focused = false }
                    cat.focused = true
                    return
                }
                startY += elementHeight
            }
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
    
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        searchElement.handleMouseRelease(
            mouseX, mouseY, state,
            windowXStart + sideWidth + 8f, windowYStart + 8f,
            windowWidth - sideWidth - 16f, windowHeight - 16f,
            categoryElements
        )
        
        if (!searchElement.isTyping()) {
            categoryElements.filter { it.focused }.forEach { cat ->
                cat.handleMouseRelease(
                    mouseX, mouseY, state,
                    windowXStart + sideWidth + 8f, windowYStart + 8f,
                    windowWidth - sideWidth - 16f, windowHeight - 16f
                )
            }
        }
        
        super.mouseReleased(mouseX, mouseY, state)
    }
    
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE && !cant) {
            closed = true
            return
        }
        
        categoryElements.filter { it.focused }.forEach { cat ->
            if (cat.handleKeyTyped(typedChar, keyCode)) return
        }
        
        searchElement.handleTyping(
            typedChar, keyCode,
            windowXStart + sideWidth + 8f, windowYStart + 8f,
            windowWidth - sideWidth - 16f, windowHeight - 16f,
            categoryElements
        )
        
        super.keyTyped(typedChar, keyCode)
    }
    
    override fun doesGuiPauseGame() = false
    
    private fun easeOutQuart(x: Float): Float {
        return 1 - (1 - x) * (1 - x) * (1 - x) * (1 - x)
    }
    
    companion object {
        private val categoryElements = mutableListOf<M3CategoryElement>()
        private val searchElement: M3SearchElement
        
        init {
            ModuleCategory.values().forEach { categoryElements.add(M3CategoryElement(it)) }
            if (categoryElements.isNotEmpty()) {
                categoryElements[0].focused = true
            }
            searchElement = M3SearchElement(0f, 0f, 90f, 20f)
        }
        
        private var instance: MaterialDesign3Gui? = null
        
        fun getInstance(): MaterialDesign3Gui {
            return instance ?: MaterialDesign3Gui().also { instance = it }
        }
        
        fun resetInstance() {
            instance = MaterialDesign3Gui()
        }
    }
}
