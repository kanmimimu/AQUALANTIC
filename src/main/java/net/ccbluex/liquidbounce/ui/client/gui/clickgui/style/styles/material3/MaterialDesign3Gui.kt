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
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class MaterialDesign3Gui : GuiScreen() {
    
    private val categoryElements = mutableListOf<M3CategoryElement>()
    
    private var windowXStart = 0f
    private var windowYStart = 0f
    private var windowXEnd = 0f
    private var windowYEnd = 0f
    
    private val windowWidth get() = windowXEnd - windowXStart
    private val windowHeight get() = windowYEnd - windowYStart
    
    private val margin = 40f
    private val maxWidth = 600f
    private val maxHeight = 450f
    
    private var sideWidth = 100f
    
    private var searchElement: M3SearchElement? = null
    
    private var closed = false
    private var animProgress = 0f
    
    private var startYAnim = 0f
    private var endYAnim = 0f
    
    init {
        ModuleCategory.values().forEach { categoryElements.add(M3CategoryElement(it)) }
        if (categoryElements.isNotEmpty()) {
            categoryElements[0].focused = true
        }
    }
    
    override fun initGui() {
        super.initGui()
        Keyboard.enableRepeatEvents(true)
        
        val calcWidth = (width - margin * 2).coerceAtMost(maxWidth)
        val calcHeight = (height - margin * 2).coerceAtMost(maxHeight)
        windowXStart = (width - calcWidth) / 2
        windowYStart = (height - calcHeight) / 2
        windowXEnd = windowXStart + calcWidth
        windowYEnd = windowYStart + calcHeight
        
        searchElement = M3SearchElement(
            windowXStart + 8f,
            windowYStart + 30f,
            sideWidth - 16f,
            20f
        )
        
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
        animProgress += (0.0075f * 0.25f * RenderUtils.deltaTime * if (closed) -1f else 1f)
        animProgress = animProgress.coerceIn(0f, 1f)
        
        if (closed && animProgress == 0f) {
            mc.displayGuiScreen(null)
            return
        }
        
        val percent = EaseUtils.easeOutBack(animProgress.toDouble()).toFloat()
        
        GL11.glPushMatrix()
        if (!ClickGUIModule.fastRenderValue.get()) {
            GL11.glScalef(percent, percent, percent)
            GL11.glTranslatef(
                ((windowXEnd * 0.5f * (1 - percent)) / percent),
                ((windowYEnd * 0.5f * (1 - percent)) / percent),
                0f
            )
        }
        
        drawFullSized(mouseX, mouseY, partialTicks)
        
        GL11.glPopMatrix()
    }
    
    private fun drawFullSized(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val accentColor = ClientTheme.getColor(1)
        
        RenderUtils.drawRoundedRect(
            windowXStart, windowYStart,
            windowXEnd, windowYEnd,
            M3Dimensions.cornerExtraLarge,
            M3Colors.surface.rgb
        )
        
        RenderUtils.customRounded(
            windowXStart, windowYStart,
            windowXStart + sideWidth, windowYEnd,
            M3Dimensions.cornerExtraLarge, 0f, M3Dimensions.cornerExtraLarge, 0f,
            M3Colors.surfaceContainer.rgb
        )
        
        val xButtonHovered = mouseX.toFloat() in (windowXEnd - 24f)..windowXEnd && 
                             mouseY.toFloat() in windowYStart..(windowYStart + 24f)
        if (xButtonHovered) {
            RenderUtils.customRounded(
                windowXEnd - 24f, windowYStart,
                windowXEnd, windowYStart + 24f,
                0f, M3Dimensions.cornerExtraLarge, 0f, 0f,
                M3Colors.errorContainer.rgb
            )
        }
        GlStateManager.disableAlpha()
        RenderUtils.drawImage(IconManager.removeIcon, (windowXEnd - 18f).toInt(), (windowYStart + 7f).toInt(), 10, 10)
        RenderUtils.drawImage(IconManager.brush, (windowXStart + 6f).toInt(), (windowYEnd - 30f).toInt(), 24, 24)
        RenderUtils.drawImage(IconManager.settings, (windowXStart + 6f).toInt(), (windowYEnd - 60f).toInt(), 24, 24)
        GlStateManager.enableAlpha()
        
        searchElement?.let { search ->
            search.xPos = windowXStart + 8f
            search.yPos = windowYStart + 30f
            search.width = sideWidth - 16f
            
            if (search.drawBox(mouseX, mouseY, accentColor)) {
                search.drawPanel(
                    mouseX, mouseY,
                    windowXStart + sideWidth, windowYStart + 20f,
                    windowWidth - sideWidth, windowHeight - 20f,
                    Mouse.getDWheel(), categoryElements, accentColor
                )
                super.drawScreen(mouseX, mouseY, partialTicks)
                return
            }
        }
        
        Fonts.SFApple40.drawString(
            categoryElements.find { it.focused }?.name ?: "Settings",
            windowXStart + 8f, windowYStart + 8f,
            M3Colors.onSurface.rgb
        )
        
        val elementsStartY = 55f
        val elementHeight = 24f
        var startY = windowYStart + elementsStartY
        var lastFocusedYStart = 0f
        var lastFocusedYEnd = 0f
        
        for (ce in categoryElements) {
            ce.drawLabel(mouseX, mouseY, windowXStart, startY, sideWidth, elementHeight, accentColor)
            if (ce.focused) {
                lastFocusedYStart = startY + 4f
                lastFocusedYEnd = startY + elementHeight - 4f
                
                startYAnim = if (ClickGUIModule.fastRenderValue.get()) {
                    startY + 4f
                } else {
                    AnimationUtils.animate(
                        startY + 4f, startYAnim,
                        (if (startYAnim - (startY + 4f) > 0) 0.65f else 0.55f) * RenderUtils.deltaTime * 0.025f
                    )
                }
                endYAnim = if (ClickGUIModule.fastRenderValue.get()) {
                    startY + elementHeight - 4f
                } else {
                    AnimationUtils.animate(
                        startY + elementHeight - 4f, endYAnim,
                        (if (endYAnim - (startY + elementHeight - 4f) < 0) 0.65f else 0.55f) * RenderUtils.deltaTime * 0.025f
                    )
                }
                
                ce.drawPanel(
                    mouseX, mouseY,
                    windowXStart + sideWidth, windowYStart + 20f,
                    windowWidth - sideWidth, windowHeight - 20f,
                    Mouse.getDWheel(), accentColor
                )
            }
            startY += elementHeight
        }
        
        RenderUtils.drawRoundedRect(
            windowXStart + 4f, startYAnim,
            windowXStart + 6f, endYAnim,
            1f, accentColor.rgb
        )
        
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        searchElement?.let { search ->
            if (search.isTyping() && MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, windowYStart, windowXStart + 60f, windowYStart + 24f)) {
                search.searchBox.text = ""
                return
            }
        }
        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXEnd - 24f, windowYStart, windowXEnd, windowYStart + 24f)) {
            mc.displayGuiScreen(null)
            return
        }
        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, windowYEnd - 40f, windowXStart + 40f, windowYEnd)) {
            mc.displayGuiScreen(GuiHudDesigner())
            return
        }
        
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, windowYEnd - 70f, windowXStart + 30f, windowYEnd - 30f)) {
            mc.displayGuiScreen(GuiClientSettings(this))
            return
        }
        
        val elementsStartY = 55f
        val elementHeight = 24f
        var startY = windowYStart + elementsStartY
        
        searchElement?.handleMouseClick(
            mouseX, mouseY, mouseButton,
            windowXStart + sideWidth, windowYStart + 20f,
            windowWidth - sideWidth, windowHeight - 20f,
            categoryElements
        )
        
        if (searchElement?.isTyping() != true) {
            categoryElements.forEach { cat ->
                if (cat.focused) {
                    cat.handleMouseClick(
                        mouseX, mouseY, mouseButton,
                        windowXStart + sideWidth, windowYStart + 20f,
                        windowWidth - sideWidth, windowHeight - 20f
                    )
                }
                
                if (MouseUtils.mouseWithinBounds(mouseX, mouseY, windowXStart, startY, windowXStart + sideWidth, startY + elementHeight) && searchElement?.isTyping() != true) {
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
        searchElement?.handleMouseRelease(
            mouseX, mouseY, state,
            windowXStart + sideWidth, windowYStart + 20f,
            windowWidth - sideWidth, windowHeight - 20f,
            categoryElements
        )
        
        if (searchElement?.isTyping() != true) {
            categoryElements.filter { it.focused }.forEach { cat ->
                cat.handleMouseRelease(
                    mouseX, mouseY, state,
                    windowXStart + sideWidth, windowYStart + 20f,
                    windowWidth - sideWidth, windowHeight - 20f
                )
            }
        }
        
        super.mouseReleased(mouseX, mouseY, state)
    }
    
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE && !cant) {
            closed = true
            if (ClickGUIModule.fastRenderValue.get()) mc.displayGuiScreen(null)
            return
        }
        
        categoryElements.filter { it.focused }.forEach { cat ->
            if (cat.handleKeyTyped(typedChar, keyCode)) return
        }
        
        searchElement?.let { search ->
            if (search.handleTyping(typedChar, keyCode, windowXStart + sideWidth, windowYStart + 20f, windowWidth - sideWidth, windowHeight - 20f, categoryElements)) {
                return
            }
        }
        
        super.keyTyped(typedChar, keyCode)
    }
    
    override fun doesGuiPauseGame() = false
    
    var cant = false
    
    companion object {
        private var instance: MaterialDesign3Gui? = null
        
        fun getInstance(): MaterialDesign3Gui {
            return instance ?: MaterialDesign3Gui().also { instance = it }
        }
        
        fun resetInstance() {
            instance = MaterialDesign3Gui()
        }
    }
}
