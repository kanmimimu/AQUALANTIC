package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.modern

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.modern.element.ModernCategoryElement
import net.ccbluex.liquidbounce.ui.client.gui.colortheme.ClientTheme
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.IOException

class ModernUi : GuiScreen() {
    private val categoryElements: MutableList<ModernCategoryElement> = ArrayList()
    
    // Layout constants
    private val windowWidth = 600f
    private val windowHeight = 400f
    private val sidebarWidth = 140f
    
    // Colors
    private val backgroundColor = Color(20, 20, 25, 240)
    private val sidebarColor = Color(30, 30, 35, 240)
    
    private var windowX = 100f
    private var windowY = 100f
    private var dragging = false
    private var dragX = 0f
    private var dragY = 0f
    
    private var searchField: GuiTextField? = null
    private var animProgress = 0f
    private var closing = false
    
    init {
        ModuleCategory.values().forEach { categoryElements.add(ModernCategoryElement(it)) }
        if (categoryElements.isNotEmpty()) {
            categoryElements[0].selected = true
        }
    }

    override fun initGui() {
        super.initGui()
        searchField = GuiTextField(0, mc.fontRendererObj, 0, 0, 120, 20)
        searchField?.maxStringLength = 20
        searchField?.enableBackgroundDrawing = false
        animProgress = 0f
        closing = false
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // Animation
        if (closing) {
            animProgress -= 0.1f * RenderUtils.deltaTime * 0.1f
            if (animProgress <= 0f) {
                mc.displayGuiScreen(null)
                return
            }
        } else {
            animProgress += 0.1f * RenderUtils.deltaTime * 0.1f
            if (animProgress > 1f) animProgress = 1f
        }
        
        val scale = EaseUtils.easeOutBack(animProgress.toDouble()).toFloat()
        
        GL11.glPushMatrix()
        GL11.glTranslated((width / 2.0), (height / 2.0), 0.0)
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslated(-(width / 2.0), -(height / 2.0), 0.0)

        // Handle dragging
        if (dragging) {
            windowX = mouseX - dragX
            windowY = mouseY - dragY
        }

        // Draw Main Window Background
        RenderUtils.drawRoundedRect(windowX, windowY, windowX + windowWidth, windowY + windowHeight, 10f, backgroundColor.rgb)
        
        // Draw Sidebar
        RenderUtils.customRounded(windowX, windowY, windowX + sidebarWidth, windowY + windowHeight, 10f, 0f, 10f, 0f, sidebarColor.rgb)
        
        // Draw Title
        Fonts.SFApple40.drawString("MintClient", windowX + 20, windowY + 20, Color.WHITE.rgb)
        Fonts.SFApple35.drawString("Modern Edition", windowX + 22, windowY + 45, Color.LIGHT_GRAY.rgb)

        // Draw Search Bar
        val searchX = windowX + 20
        val searchY = windowY + windowHeight - 40
        RenderUtils.drawRoundedRect(searchX, searchY, searchX + 100, searchY + 20, 5f, Color(50, 50, 60, 200).rgb)
        searchField?.xPosition = (searchX + 5).toInt()
        searchField?.yPosition = (searchY + 6).toInt()
        searchField?.drawTextBox()
        if (searchField?.text?.isEmpty() == true && !searchField!!.isFocused) {
            Fonts.SFApple35.drawString("Search...", searchX + 5, searchY + 5, Color.GRAY.rgb)
        }

        // Draw Categories
        var catY = windowY + 80f
        for (category in categoryElements) {
            category.draw(mouseX, mouseY, windowX, catY, sidebarWidth)
            catY += 40f
        }
        
        // Draw Modules for selected category
        val selectedCategory = categoryElements.find { it.selected }
        selectedCategory?.drawModules(mouseX, mouseY, windowX + sidebarWidth + 10, windowY + 10, windowWidth - sidebarWidth - 20, windowHeight - 20, searchField?.text ?: "")

        GL11.glPopMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        searchField?.mouseClicked(mouseX, mouseY, mouseButton)
        
        // Dragging logic
        if (mouseX >= windowX && mouseX <= windowX + windowWidth && mouseY >= windowY && mouseY <= windowY + 30) {
            dragging = true
            dragX = mouseX - windowX
            dragY = mouseY - windowY
            return
        }

        // Category clicking
        var catY = windowY + 80f
        for (category in categoryElements) {
            if (mouseX >= windowX && mouseX <= windowX + sidebarWidth && mouseY >= catY && mouseY <= catY + 35) {
                categoryElements.forEach { it.selected = false }
                category.selected = true
                return
            }
            catY += 40f
        }

        // Module clicking
        val selectedCategory = categoryElements.find { it.selected }
        selectedCategory?.mouseClicked(mouseX, mouseY, mouseButton, windowX + sidebarWidth + 10, windowY + 10, windowWidth - sidebarWidth - 20, windowHeight - 20, searchField?.text ?: "")
        
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
    
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (searchField?.isFocused == true) {
            searchField?.textboxKeyTyped(typedChar, keyCode)
            return
        }
        
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closing = true
            return
        }
        super.keyTyped(typedChar, keyCode)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        dragging = false
        super.mouseReleased(mouseX, mouseY, state)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
    
    companion object {
        private var instance: ModernUi? = null
        fun getInstance(): ModernUi {
            if (instance == null) {
                instance = ModernUi()
            }
            return instance!!
        }
    }
}
