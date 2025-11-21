package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.modern.element

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color

class ModernCategoryElement(val category: ModuleCategory) {
    var selected = false
    private val modules = ArrayList<ModernModuleElement>()
    private var scrollY = 0f
    
    init {
        CrossSine.moduleManager.getModuleInCategory(category).forEach {
            modules.add(ModernModuleElement(it))
        }
    }

    fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        val height = 35f
        val hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
        
        val color = if (selected) Color(60, 60, 70, 200) else if (hover) Color(50, 50, 60, 150) else Color(0, 0, 0, 0)
        
        RenderUtils.drawRoundedRect(x + 10, y, x + width - 10, y + height, 8f, color.rgb)
        
        val textColor = if (selected) Color.WHITE else Color.GRAY
        Fonts.SFApple35.drawString(category.displayName, x + 25, y + 10, textColor.rgb)
    }

    fun drawModules(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float, searchQuery: String) {
        val filteredModules = modules.filter { it.module.name.contains(searchQuery, ignoreCase = true) }
        
        // Scroll logic
        val dWheel = Mouse.getDWheel()
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if (dWheel < 0) scrollY -= 20f
            else if (dWheel > 0) scrollY += 20f
        }
        
        // Calculate total height
        var totalHeight = 0f
        val modWidth = 130f
        val gap = 10f
        var currentX = x
        var currentY = y
        
        for (module in filteredModules) {
             if (currentX + modWidth > x + width) {
                currentX = x
                currentY += module.getHeight() + gap
            }
            currentX += modWidth + gap
        }
        totalHeight = currentY - y + 50f // Approximate
        
        if (scrollY > 0) scrollY = 0f
        if (scrollY < -totalHeight + height) scrollY = -totalHeight + height
        if (totalHeight < height) scrollY = 0f

        GL11.glPushMatrix()
        RenderUtils.makeScissorBox(x, y, x + width, y + height)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        
        var modY = y + scrollY
        var modX = x
        
        for (module in filteredModules) {
            val modHeight = module.getHeight()
            
            if (modX + modWidth > x + width) {
                modX = x
                modY += modHeight + gap
            }
            
            module.draw(mouseX, mouseY, modX, modY, modWidth, modHeight)
            modX += modWidth + gap
        }
        
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()
    }
    
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float, height: Float, searchQuery: String) {
        val filteredModules = modules.filter { it.module.name.contains(searchQuery, ignoreCase = true) }
        
        var modY = y + scrollY
        var modX = x
        val modWidth = 130f
        val gap = 10f
        
        for (module in filteredModules) {
            val modHeight = module.getHeight()
            
            if (modX + modWidth > x + width) {
                modX = x
                modY += modHeight + gap
            }
            
            if (mouseX >= modX && mouseX <= modX + modWidth && mouseY >= modY && mouseY <= modY + modHeight) {
                // Check if click is within scissor box
                if (mouseY >= y && mouseY <= y + height) {
                    module.mouseClicked(mouseX, mouseY, mouseButton, modX, modY, modWidth, modHeight)
                }
                return
            }
            
            modX += modWidth + gap
        }
    }
}
