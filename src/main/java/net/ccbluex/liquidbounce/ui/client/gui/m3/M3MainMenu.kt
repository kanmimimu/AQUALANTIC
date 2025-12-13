package net.ccbluex.liquidbounce.ui.client.gui.m3

import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3.M3Colors
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.shader.Shader
import net.minecraft.client.gui.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.awt.Color

class M3MainMenu : GuiScreen(), GuiYesNoCallback {

    private lateinit var buttons: List<M3Button>
    
    private val backgroundShader = Shader("background.frag")
    private var initTime = System.currentTimeMillis()
    
    private var openProgress = 0f

    override fun initGui() {
        val buttonWidth = 120f
        val buttonHeight = 40f
        val buttonSpacing = 12f
        val totalButtons = 5
        val totalWidth = buttonWidth * totalButtons + buttonSpacing * (totalButtons - 1)
        val startX = (width - totalWidth) / 2f
        val buttonY = height / 2f + 40f

        buttons = listOf(
            M3Button(1, startX, buttonY, buttonWidth, buttonHeight, "SinglePlayer", M3ButtonStyle.FILLED_TONAL),
            M3Button(2, startX + (buttonWidth + buttonSpacing), buttonY, buttonWidth, buttonHeight, "MultiPlayer", M3ButtonStyle.FILLED_TONAL),
            M3Button(3, startX + (buttonWidth + buttonSpacing) * 2, buttonY, buttonWidth, buttonHeight, "AltManager", M3ButtonStyle.FILLED_TONAL),
            M3Button(4, startX + (buttonWidth + buttonSpacing) * 3, buttonY, buttonWidth, buttonHeight, "Options", M3ButtonStyle.FILLED_TONAL),
            M3Button(5, startX + (buttonWidth + buttonSpacing) * 4, buttonY, buttonWidth, buttonHeight, "Quit", M3ButtonStyle.FILLED_TONAL)
        )

        initTime = System.currentTimeMillis()
        openProgress = 0f
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val deltaTime = RenderUtils.deltaTime.toFloat().coerceIn(1f, 50f)
        openProgress = (openProgress + 0.005f * deltaTime).coerceAtMost(1f)
        val easedProgress = easeOutQuart(openProgress)

        backgroundShader.use()
        GL20.glUniform2f(backgroundShader.getUniform("resolution"), width.toFloat(), height.toFloat())
        GL20.glUniform1f(backgroundShader.getUniform("time"), (System.currentTimeMillis() - initTime) / 1000f)
        RenderUtils.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Color.BLACK.rgb)
        backgroundShader.unuse()

        GL11.glPushMatrix()
        
        val titleY = height / 2f - 60f
        val slideOffset = (1f - easedProgress) * 30f
        val alpha = (easedProgress * 255).toInt().coerceIn(0, 255)
        
        val titleFont = Fonts.font40
        titleFont.drawCenteredString(
            "AQUALANTIC",
            width / 2f,
            titleY - slideOffset,
            M3Colors.withAlpha(Color.WHITE, alpha).rgb,
            true
        )
        
        val subtitleFont = Fonts.font35
        subtitleFont.drawCenteredString(
            "Material Design 3",
            width / 2f,
            titleY + 25f - slideOffset,
            M3Colors.withAlpha(M3Colors.outlineVariant, alpha).rgb,
            false
        )

        for ((index, button) in buttons.withIndex()) {
            val buttonSlideOffset = (1f - easedProgress) * (50f + index * 10f)
            val originalY = button.y
            button.y = originalY + buttonSlideOffset
            button.draw(mouseX, mouseY, partialTicks)
            button.y = originalY
        }

        GL11.glPopMatrix()

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (mouseButton == 0) {
            for (button in buttons) {
                if (button.mousePressed(mouseX, mouseY, mouseButton)) {
                    break
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        for (button in buttons) {
            if (button.mouseReleased(mouseX, mouseY, state)) {
                handleButtonClick(button.id)
                break
            }
        }
        super.mouseReleased(mouseX, mouseY, state)
    }

    private fun handleButtonClick(id: Int) {
        when (id) {
            1 -> mc.displayGuiScreen(GuiSelectWorld(this))
            2 -> mc.displayGuiScreen(GuiMultiplayer(this))
            3 -> mc.displayGuiScreen(GuiAltManager(this))
            4 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            5 -> mc.shutdown()
        }
    }

    private fun easeOutQuart(x: Float): Float {
        return 1f - (1f - x).let { it * it * it * it }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}
