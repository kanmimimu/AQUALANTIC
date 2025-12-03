package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.CrossSine
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.ListValue
import net.ccbluex.liquidbounce.features.value.TitleValue
import net.ccbluex.liquidbounce.ui.client.gui.colortheme.ClientTheme
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiChat
import org.lwjgl.input.Keyboard
import java.awt.Color

@ModuleInfo(name = "Interface", category = ModuleCategory.VISUAL, array = false, defaultOn = true)
object Interface : Module() {
    val watermark = ListValue("Watermark", arrayOf("AQUALANTIC", "aquasense"), "AQUALANTIC")

    val title = TitleValue("AAAAAAAAAAAAAAAAAAAAAA")
    val buttonValue = BoolValue("ContainerButton", false)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    val inventoryAnimation = BoolValue("InventoryAnimation", false)
    val noF5 = BoolValue("NoF5-Crosshair", false)
    val shaders = BoolValue("Shader", true)

    @EventTarget
    fun onTick(event: TickEvent) {
        mc.guiAchievement.clearAchievements()
        if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD) && mc.currentScreen == null) {
            mc.displayGuiScreen(GuiChat("."))
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner) return
        CrossSine.hud.render(false, event.partialTicks)
        when (watermark.get()) {
            "AQUALANTIC" -> {
                val posX = 5
                val posY = 5
                val borderThickness = 1
                val internalPaddingX = 4
                val internalPaddingY = 2
                val font = Fonts.M700_40

                val textsToRender = mutableListOf<String>()
                textsToRender.add("AQUALANTIC") // 幅計算用にカラーコードなしのテキストを使用
                textsToRender.add("§7B${CrossSine.CLIENT_VERSION}")
                textsToRender.add("§7${mc.session.username}")
                if (!mc.isSingleplayer) {
                    textsToRender.add("§7${ServerUtils.getRemoteIp()}")
                }

                val separator = " §7| "
                val fullTextForWidth = textsToRender.joinToString(separator)
                val combinedTextWidth = font.getStringWidth(fullTextForWidth.replace("§.".toRegex(), ""))

                val innerBackgroundWidth = combinedTextWidth + 2 * internalPaddingX
                val innerBackgroundHeight = font.FONT_HEIGHT + 2 * internalPaddingY
                val totalWidth = innerBackgroundWidth + 2 * borderThickness
                val totalHeight = innerBackgroundHeight + 2 * borderThickness

                RenderUtils.drawRectBordered(posX.toDouble(), posY.toDouble(), (posX + totalWidth).toDouble(), (posY + totalHeight).toDouble(), borderThickness.toDouble(), Color(0, 0, 0, 255).rgb, Color(0x414141).rgb)
                
                var currentTextX = (posX + borderThickness + internalPaddingX).toFloat()
                val currentTextY = (posY + borderThickness + internalPaddingY).toFloat()
                val unifiedThemeColor = ClientTheme.getColor(0, true)

                // "AQUALANTIC" をカスタムカラーで描画
                val clientColor = unifiedThemeColor.rgb
                val whiteColor = Color.WHITE.rgb

                // "A" (白、太字)
                font.drawString("A", currentTextX, currentTextY, whiteColor, true)
                currentTextX += font.getStringWidth("A")

                // "Q" (クライアントカラー、太字)
                font.drawString("Q", currentTextX, currentTextY, clientColor, true)
                currentTextX += font.getStringWidth("Q")

                // "UA" (白、太字)
                font.drawString("UA", currentTextX, currentTextY, whiteColor, true)
                currentTextX += font.getStringWidth("UA")

                // "LANTIC" (クライアントカラー、太字)
                font.drawString("LANTIC", currentTextX, currentTextY, clientColor, true)
                currentTextX += font.getStringWidth("LANTIC")

                // 残りのテキストを描画 (バージョン、ユーザー名など)
                for (i in 1 until textsToRender.size) {
                    font.drawString(separator, currentTextX, currentTextY, Color.GRAY.rgb, true)
                    currentTextX += font.getStringWidth(separator)

                    val text = textsToRender[i]
                    font.drawString(text, currentTextX, currentTextY, Color.WHITE.rgb, true)
                    currentTextX += font.getStringWidth(text.replace("§.".toRegex(), ""))
                }

                val underlineThickness = 1.0f
                val underlineYOffset = 1.0f
                val underlineY = (posY + borderThickness + internalPaddingY) + font.FONT_HEIGHT + underlineYOffset
                RenderUtils.drawRect((posX + borderThickness).toFloat(), underlineY, (posX + borderThickness + innerBackgroundWidth).toFloat(), underlineY + underlineThickness, unifiedThemeColor.rgb)
            }
            "aquasense" -> {
                val posX = 5
                val posY = 5
                val borderThickness = 1
                val internalPaddingX = 4
                val internalPaddingY = 2
                val font = Fonts.M700_40

                val textsToRender = mutableListOf<String>()
                textsToRender.add("aquasense") // 幅計算用にカラーコードなしのテキストを使用
                textsToRender.add("§7B${CrossSine.CLIENT_VERSION}")
                textsToRender.add("§7${mc.session.username}")
                if (!mc.isSingleplayer) {
                    textsToRender.add("§7${ServerUtils.getRemoteIp()}")
                }

                val separator = " §7| "
                val fullTextForWidth = textsToRender.joinToString(separator)
                val combinedTextWidth = font.getStringWidth(fullTextForWidth.replace("§.".toRegex(), ""))

                val innerBackgroundWidth = combinedTextWidth + 2 * internalPaddingX
                val innerBackgroundHeight = font.FONT_HEIGHT + 2 * internalPaddingY
                val totalWidth = innerBackgroundWidth + 2 * borderThickness
                val totalHeight = innerBackgroundHeight + 2 * borderThickness

                RenderUtils.drawRectBordered(posX.toDouble(), posY.toDouble(), (posX + totalWidth).toDouble(), (posY + totalHeight).toDouble(), borderThickness.toDouble(), Color(0, 0, 0, 255).rgb, Color(0x414141).rgb)

                var currentTextX = (posX + borderThickness + internalPaddingX).toFloat()
                val currentTextY = (posY + borderThickness + internalPaddingY).toFloat()
                val unifiedThemeColor = ClientTheme.getColor(0, true)

                // "AQUALANTIC" をカスタムカラーで描画
                val clientColor = unifiedThemeColor.rgb
                val whiteColor = Color.WHITE.rgb

                // "A" (白、太字)
                font.drawString("a", currentTextX, currentTextY, whiteColor, true)
                currentTextX += font.getStringWidth("a")

                // "Q" (クライアントカラー、太字)
                font.drawString("q", currentTextX, currentTextY, clientColor, true)
                currentTextX += font.getStringWidth("q")

                // "UA" (白、太字)
                font.drawString("ua", currentTextX, currentTextY, whiteColor, true)
                currentTextX += font.getStringWidth("ua")

                // "LANTIC" (クライアントカラー、太字)
                font.drawString("sense", currentTextX, currentTextY, clientColor, true)
                currentTextX += font.getStringWidth("sense")

                // 残りのテキストを描画 (バージョン、ユーザー名など)
                for (i in 1 until textsToRender.size) {
                    font.drawString(separator, currentTextX, currentTextY, Color.GRAY.rgb, true)
                    currentTextX += font.getStringWidth(separator)

                    val text = textsToRender[i]
                    font.drawString(text, currentTextX, currentTextY, Color.WHITE.rgb, true)
                    currentTextX += font.getStringWidth(text.replace("§.".toRegex(), ""))
                }

                val underlineThickness = 1.0f
                val underlineYOffset = 1.0f
                val underlineY = (posY + borderThickness + internalPaddingY) + font.FONT_HEIGHT + underlineYOffset
                RenderUtils.drawRect((posX + borderThickness).toFloat(), underlineY, (posX + borderThickness + innerBackgroundWidth).toFloat(), underlineY + underlineThickness, unifiedThemeColor.rgb)
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        CrossSine.hud.update()
    }

    @EventTarget
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return
        }

    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        CrossSine.hud.handleKey('a', event.key)
    }

}
