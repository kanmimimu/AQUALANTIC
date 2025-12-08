package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * M3 Navigation Rail Category Item
 */
class M3CategoryItem(val category: ModuleCategory) {
    var selected = false
    private var hoverProgress = 0f
    private var selectProgress = 0f
    
    private val modules: List<Module> = CrossSine.moduleManager.getModuleInCategory(category)
    private val moduleElements = modules.map { M3ModuleCard(it) }
    
    private var scrollY = 0f
    private var targetScrollY = 0f
    
    fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float): Float {
        val height = M3Dimensions.navRailItemHeight
        val isHovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
        
        // Animate hover
        val hoverTarget = if (isHovered) 1f else 0f
        hoverProgress += (hoverTarget - hoverProgress) * 0.2f
        
        // Animate selection
        val selectTarget = if (selected) 1f else 0f
        selectProgress += (selectTarget - selectProgress) * 0.15f
        
        // Draw indicator pill (selected state)
        if (selectProgress > 0.01f) {
            val pillWidth = 56f * selectProgress
            val pillHeight = 32f
            val pillX = x + (width - pillWidth) / 2
            val pillY = y + (height - pillHeight) / 2
            
            RenderUtils.drawRoundedRect(
                pillX, pillY, 
                pillX + pillWidth, pillY + pillHeight, 
                pillHeight / 2,  // Proper pill radius
                M3Colors.withAlpha(M3Colors.secondaryContainer, (255 * selectProgress).toInt()).rgb
            )
        }
        
        // Draw hover overlay
        if (hoverProgress > 0.01f && !selected) {
            val hoverColor = M3Colors.withAlpha(M3Colors.onSurfaceVariant, (20 * hoverProgress).toInt())
            RenderUtils.drawRoundedRect(
                x + 12, y + 4,
                x + width - 12, y + height - 4,
                M3Dimensions.cornerLarge,
                hoverColor.rgb
            )
        }
        
        // Draw category name
        val textColor = if (selected) M3Colors.onSecondaryContainer else M3Colors.onSurfaceVariant
        val displayName = category.displayName
        val textWidth = Fonts.SFApple35.getStringWidth(displayName)
        Fonts.SFApple35.drawString(
            displayName,
            x + (width - textWidth) / 2,
            y + height / 2 - 4,
            textColor.rgb
        )
        
        return height
    }
    
    fun drawModules(
        mouseX: Int, mouseY: Int,
        x: Float, y: Float,
        width: Float, height: Float,
        searchQuery: String
    ) {
        val filteredModules = if (searchQuery.isEmpty()) {
            moduleElements
        } else {
            moduleElements.filter { 
                it.module.name.contains(searchQuery, ignoreCase = true)
            }
        }
        
        // Smooth scroll
        val dWheel = Mouse.getDWheel()
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if (dWheel != 0) {
                targetScrollY += if (dWheel > 0) 40f else -40f
            }
        }
        scrollY += (targetScrollY - scrollY) * 0.2f
        
        // Calculate total content height
        var totalHeight = M3Dimensions.spacingMd
        for (module in filteredModules) {
            totalHeight += module.getHeight() + M3Dimensions.spacingSm
        }
        
        // Clamp scroll
        val maxScroll = 0f
        val minScroll = -(totalHeight - height + M3Dimensions.spacingLg).coerceAtLeast(0f)
        targetScrollY = targetScrollY.coerceIn(minScroll, maxScroll)
        
        // Begin scissor
        GL11.glPushMatrix()
        RenderUtils.makeScissorBox(x, y, x + width, y + height)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        
        // Draw modules
        var currentY = y + scrollY + M3Dimensions.spacingMd
        for (moduleElement in filteredModules) {
            val cardHeight = moduleElement.getHeight()
            moduleElement.draw(mouseX, mouseY, x + M3Dimensions.spacingMd, currentY, width - M3Dimensions.spacingMd * 2)
            currentY += cardHeight + M3Dimensions.spacingSm
        }
        
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()
    }
    
    fun mouseClicked(
        mouseX: Int, mouseY: Int, mouseButton: Int,
        x: Float, y: Float,
        width: Float, height: Float,
        searchQuery: String
    ) {
        val filteredModules = if (searchQuery.isEmpty()) {
            moduleElements
        } else {
            moduleElements.filter { 
                it.module.name.contains(searchQuery, ignoreCase = true)
            }
        }
        
        if (mouseY < y || mouseY > y + height) return
        
        var currentY = y + scrollY + M3Dimensions.spacingMd
        for (moduleElement in filteredModules) {
            val cardHeight = moduleElement.getHeight()
            val cardX = x + M3Dimensions.spacingMd
            val cardWidth = width - M3Dimensions.spacingMd * 2
            
            if (mouseX >= cardX && mouseX <= cardX + cardWidth && 
                mouseY >= currentY && mouseY <= currentY + cardHeight &&
                mouseY >= y && mouseY <= y + height) {
                moduleElement.mouseClicked(mouseX, mouseY, mouseButton, cardX, currentY, cardWidth)
                return
            }
            currentY += cardHeight + M3Dimensions.spacingSm
        }
    }
    
    fun mouseReleased(mouseX: Int, mouseY: Int, mouseButton: Int) {
        moduleElements.forEach { it.mouseReleased() }
    }
    
    fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        moduleElements.forEach { 
            if (it.keyTyped(typedChar, keyCode)) return true
        }
        return false
    }
    
    // Global search support
    fun getModulesMatching(query: String): List<M3ModuleCard> {
        return moduleElements.filter { 
            it.module.name.contains(query, ignoreCase = true)
        }
    }
}
