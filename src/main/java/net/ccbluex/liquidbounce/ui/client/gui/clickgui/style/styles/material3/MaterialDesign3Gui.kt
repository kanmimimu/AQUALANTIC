package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.gui.ClickGUIModule
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Material Design 3 ClickGUI
 * Auto-resizing, global search, no dragging
 */
class MaterialDesign3Gui : GuiScreen() {
    
    // Layout - computed dynamically
    private var windowWidth = 0f
    private var windowHeight = 0f
    private var windowX = 0f
    private var windowY = 0f
    private val margin = 40f  // Margin from screen edges
    
    // Categories
    private val categoryItems = ModuleCategory.values().map { M3CategoryItem(it) }
    
    // State
    private var animProgress = 0f
    private var closing = false
    
    // Search
    private var searchField: GuiTextField? = null
    private var searchQuery = ""
    
    // Global search results
    private var searchResultModules = mutableListOf<M3ModuleCard>()
    private var isSearching = false
    
    init {
        if (categoryItems.isNotEmpty()) {
            categoryItems[0].selected = true
        }
    }
    
    override fun initGui() {
        super.initGui()
        
        // Auto-size window with margins
        windowWidth = (width - margin * 2).coerceAtMost(600f)
        windowHeight = (height - margin * 2).coerceAtMost(450f)
        windowX = (width - windowWidth) / 2
        windowY = (height - windowHeight) / 2
        
        // Initialize search field
        searchField = GuiTextField(0, mc.fontRendererObj, 0, 0, 200, 20).apply {
            maxStringLength = 50
            enableBackgroundDrawing = false
        }
        
        animProgress = 0f
        closing = false
    }
    
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // Animation
        if (closing) {
            animProgress -= 0.04f
            if (animProgress <= 0f) {
                mc.displayGuiScreen(null)
                return
            }
        } else {
            animProgress += 0.06f
            if (animProgress > 1f) animProgress = 1f
        }
        
        val scale = EaseUtils.easeOutBack(animProgress.toDouble()).toFloat().coerceIn(0.01f, 1f)
        
        // Draw dimmed background
        drawDefaultBackground()
        
        GL11.glPushMatrix()
        
        // Scale animation from center
        val centerX = windowX + windowWidth / 2
        val centerY = windowY + windowHeight / 2
        GL11.glTranslatef(centerX, centerY, 0f)
        GL11.glScalef(scale, scale, 1f)
        GL11.glTranslatef(-centerX, -centerY, 0f)
        
        // Main window background
        RenderUtils.drawRoundedRect(
            windowX, windowY,
            windowX + windowWidth, windowY + windowHeight,
            M3Dimensions.cornerExtraLarge,
            M3Colors.surface.rgb
        )
        
        // Navigation Rail background
        val navRailWidth = M3Dimensions.navRailWidth
        RenderUtils.customRounded(
            windowX, windowY,
            windowX + navRailWidth, windowY + windowHeight,
            M3Dimensions.cornerExtraLarge, 0f, M3Dimensions.cornerExtraLarge, 0f,
            M3Colors.surfaceContainer.rgb
        )
        
        // Draw title in nav rail
        Fonts.SFApple40.drawString(
            "Settings",
            windowX + 8,
            windowY + 16,
            M3Colors.onSurface.rgb
        )
        
        // Draw categories in nav rail
        var categoryY = windowY + 50f
        for (item in categoryItems) {
            val itemHeight = item.draw(mouseX, mouseY, windowX, categoryY, navRailWidth)
            categoryY += itemHeight
        }
        
        // Content area
        val contentX = windowX + navRailWidth
        val contentY = windowY
        val contentWidth = windowWidth - navRailWidth
        val contentHeight = windowHeight
        
        // Search bar
        val searchBarX = contentX + M3Dimensions.spacingMd
        val searchBarY = contentY + M3Dimensions.spacingMd
        val searchBarWidth = contentWidth - M3Dimensions.spacingMd * 2
        val searchBarHeight = 32f
        
        // Search bar background
        RenderUtils.drawRoundedRect(
            searchBarX, searchBarY,
            searchBarX + searchBarWidth, searchBarY + searchBarHeight,
            searchBarHeight / 2,
            M3Colors.surfaceContainerHigh.rgb
        )
        
        // Search icon
        Fonts.SFApple35.drawString("🔍", searchBarX + 10, searchBarY + 10, M3Colors.onSurfaceVariant.rgb)
        
        // Search text
        searchField?.let { field ->
            field.xPosition = (searchBarX + 34).toInt()
            field.yPosition = (searchBarY + 10).toInt()
            field.width = (searchBarWidth - 50).toInt()
            
            val prevQuery = searchQuery
            searchQuery = field.text
            
            // Update global search when query changes
            if (searchQuery != prevQuery) {
                updateGlobalSearch()
            }
            
            if (field.text.isEmpty() && !field.isFocused) {
                Fonts.SFApple35.drawString(
                    "Search all modules...",
                    searchBarX + 34, searchBarY + 10,
                    M3Colors.onSurfaceVariant.rgb
                )
            } else {
                Fonts.SFApple35.drawString(
                    field.text,
                    searchBarX + 34, searchBarY + 10,
                    M3Colors.onSurface.rgb
                )
            }
        }
        
        // Module list area
        val moduleAreaY = searchBarY + searchBarHeight + M3Dimensions.spacingSm
        val moduleAreaHeight = contentHeight - searchBarHeight - M3Dimensions.spacingMd * 2 - M3Dimensions.spacingSm
        
        // Draw modules based on search state
        if (isSearching && searchQuery.isNotEmpty()) {
            // Global search - draw from all categories
            drawGlobalSearchResults(mouseX, mouseY, contentX, moduleAreaY, contentWidth, moduleAreaHeight)
        } else {
            // Normal mode - draw selected category
            val selectedCategory = categoryItems.find { it.selected }
            selectedCategory?.drawModules(
                mouseX, mouseY,
                contentX, moduleAreaY,
                contentWidth, moduleAreaHeight,
                ""  // No filter, we already handle search globally
            )
        }
        
        GL11.glPopMatrix()
        
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
    
    private fun updateGlobalSearch() {
        isSearching = searchQuery.isNotEmpty()
        if (isSearching) {
            searchResultModules.clear()
            for (category in categoryItems) {
                searchResultModules.addAll(category.getModulesMatching(searchQuery))
            }
        }
    }
    
    private fun drawGlobalSearchResults(
        mouseX: Int, mouseY: Int,
        x: Float, y: Float,
        width: Float, height: Float
    ) {
        if (searchResultModules.isEmpty()) {
            Fonts.SFApple35.drawString(
                "No modules found",
                x + width / 2 - 40, y + 20,
                M3Colors.onSurfaceVariant.rgb
            )
            return
        }
        
        // Simple scroll-less list for now
        GL11.glPushMatrix()
        RenderUtils.makeScissorBox(x, y, x + width, y + height)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        
        var currentY = y + M3Dimensions.spacingMd
        for (module in searchResultModules.take(20)) {  // Limit visible
            val cardHeight = module.getHeight()
            if (currentY + cardHeight > y + height) break
            
            module.draw(mouseX, mouseY, x + M3Dimensions.spacingMd, currentY, width - M3Dimensions.spacingMd * 2)
            currentY += cardHeight + M3Dimensions.spacingSm
        }
        
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()
    }
    
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        searchField?.mouseClicked(mouseX, mouseY, mouseButton)
        
        // Check category clicks
        val navRailWidth = M3Dimensions.navRailWidth
        var categoryY = windowY + 50f
        for (item in categoryItems) {
            val itemHeight = M3Dimensions.navRailItemHeight
            if (mouseX >= windowX && mouseX <= windowX + navRailWidth &&
                mouseY >= categoryY && mouseY <= categoryY + itemHeight) {
                if (mouseButton == 0) {
                    categoryItems.forEach { it.selected = false }
                    item.selected = true
                    // Clear search when switching categories
                    searchField?.text = ""
                    searchQuery = ""
                    isSearching = false
                    return
                }
            }
            categoryY += itemHeight
        }
        
        // Module area
        val contentX = windowX + navRailWidth
        val contentY = windowY
        val contentWidth = windowWidth - navRailWidth
        val contentHeight = windowHeight
        
        val searchBarHeight = 32f
        val moduleAreaY = contentY + M3Dimensions.spacingMd + searchBarHeight + M3Dimensions.spacingSm
        val moduleAreaHeight = contentHeight - searchBarHeight - M3Dimensions.spacingMd * 2 - M3Dimensions.spacingSm
        
        if (isSearching && searchQuery.isNotEmpty()) {
            // Handle clicks on global search results
            var currentY = moduleAreaY + M3Dimensions.spacingMd
            for (module in searchResultModules.take(20)) {
                val cardHeight = module.getHeight()
                val cardX = contentX + M3Dimensions.spacingMd
                val cardWidth = contentWidth - M3Dimensions.spacingMd * 2
                
                if (mouseX >= cardX && mouseX <= cardX + cardWidth &&
                    mouseY >= currentY && mouseY <= currentY + cardHeight) {
                    module.mouseClicked(mouseX, mouseY, mouseButton, cardX, currentY, cardWidth)
                    return
                }
                currentY += cardHeight + M3Dimensions.spacingSm
            }
        } else {
            val selectedCategory = categoryItems.find { it.selected }
            selectedCategory?.mouseClicked(
                mouseX, mouseY, mouseButton,
                contentX, moduleAreaY,
                contentWidth, moduleAreaHeight,
                ""
            )
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
    
    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        categoryItems.forEach { it.mouseReleased(mouseX, mouseY, state) }
        searchResultModules.forEach { it.mouseReleased() }
        super.mouseReleased(mouseX, mouseY, state)
    }
    
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        // Handle search field
        if (searchField?.isFocused == true) {
            searchField?.textboxKeyTyped(typedChar, keyCode)
            if (keyCode == Keyboard.KEY_ESCAPE) {
                searchField?.isFocused = false
            }
            return
        }
        
        // Handle value controls
        if (isSearching) {
            for (module in searchResultModules) {
                if (module.keyTyped(typedChar, keyCode)) return
            }
        } else {
            val selectedCategory = categoryItems.find { it.selected }
            if (selectedCategory?.keyTyped(typedChar, keyCode) == true) {
                return
            }
        }
        
        // Close on ESC
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closing = true
            return
        }
        
        super.keyTyped(typedChar, keyCode)
    }
    
    override fun onGuiClosed() {
        CrossSine.fileManager.saveConfig(CrossSine.clickGuiConfig)
    }
    
    override fun doesGuiPauseGame(): Boolean = false
    
    companion object {
        private var instance: MaterialDesign3Gui? = null
        
        fun getInstance(): MaterialDesign3Gui {
            if (instance == null) {
                instance = MaterialDesign3Gui()
            }
            return instance!!
        }
        
        fun resetInstance() {
            instance = null
        }
    }
}
