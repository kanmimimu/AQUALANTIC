package net.ccbluex.liquidbounce.ui.client.gui

import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.shader.Shader
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL20
import java.awt.Color

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    private lateinit var singlePlayerButton: ModernButton
    private lateinit var multiPlayerButton: ModernButton
    private lateinit var altManagerButton: ModernButton
    private lateinit var settingsButton: ModernButton
    private lateinit var quitButton: ModernButton

    private lateinit var buttons: List<ModernButton>

    private val backgroundShader = Shader("background.frag")
    private var initTime = System.currentTimeMillis()

    override fun initGui() {
        val buttonWidth = 200
        val buttonHeight = 40
        val buttonSpacing = 15
        val startY = height / 2 - (buttonHeight * 5 + buttonSpacing * 4) / 2

        singlePlayerButton = ModernButton(1, width / 2 - buttonWidth / 2, startY, buttonWidth, buttonHeight, "SinglePlayer")
        multiPlayerButton = ModernButton(2, width / 2 - buttonWidth / 2, startY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight, "MultiPlayer")
        altManagerButton = ModernButton(3, width / 2 - buttonWidth / 2, startY + (buttonHeight + buttonSpacing) * 2, buttonWidth, buttonHeight, "AltManager")
        settingsButton = ModernButton(4, width / 2 - buttonWidth / 2, startY + (buttonHeight + buttonSpacing) * 3, buttonWidth, buttonHeight, "Options")
        quitButton = ModernButton(5, width / 2 - buttonWidth / 2, startY + (buttonHeight + buttonSpacing) * 4, buttonWidth, buttonHeight, "Quit")
        
        buttons = listOf(singlePlayerButton, multiPlayerButton, altManagerButton, settingsButton, quitButton)

        initTime = System.currentTimeMillis()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // Draw Shader Background
        backgroundShader.use()
        GL20.glUniform2f(backgroundShader.getUniform("resolution"), width.toFloat(), height.toFloat())
        GL20.glUniform1f(backgroundShader.getUniform("time"), (System.currentTimeMillis() - initTime) / 1000f)
        RenderUtils.drawRect(0F, 0F, width.toFloat(), height.toFloat(), Color.BLACK.rgb)
        backgroundShader.unuse()

        // Draw Title
        val title = "MintClient"
        val titleFont = Fonts.font40
        titleFont.drawCenteredString(title, width / 2f, height / 2f - 150, Color.WHITE.rgb, true)

        // Draw Buttons
        for (button in buttons) {
            button.draw(mouseX, mouseY, partialTicks)
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            for (button in buttons) {
                if (button.isHovered(mouseX, mouseY)) {
                    when (button.id) {
                        1 -> mc.displayGuiScreen(GuiSelectWorld(this))
                        2 -> mc.displayGuiScreen(GuiMultiplayer(this))
                        3 -> mc.displayGuiScreen(GuiAltManager(this))
                        4 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
                        5 -> mc.shutdown()
                    }
                }
            }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}