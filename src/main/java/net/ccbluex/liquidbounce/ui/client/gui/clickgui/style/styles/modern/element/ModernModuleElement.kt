package net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.modern.element

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.value.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import java.awt.Color

class ModernModuleElement(val module: Module) {
    var expanded = false
    
    fun getHeight(): Float {
        var h = 40f
        if (expanded) {
            for (value in module.values) {
                if (value is BoolValue || value is ListValue || value is FloatValue || value is IntegerValue || value is TextValue) {
                    h += 25f
                }
            }
            h += 5f
        }
        return h
    }
    
    fun draw(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float) {
        val enabled = module.state
        val hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 40f
        
        val bgColor = if (enabled) Color(40, 100, 200, 200) else Color(40, 40, 45, 200)
        val borderColor = if (hover) Color(100, 100, 100, 100) else Color(0, 0, 0, 0)
        
        RenderUtils.drawRoundedRect(x, y, x + width, y + 40f, 6f, bgColor.rgb)
        if (hover) {
             RenderUtils.drawBorderedRect(x, y, x + width, y + 40f, 2f, Color(0,0,0,0).rgb, borderColor.rgb)
        }
        
        val textColor = if (enabled) Color.WHITE else Color.LIGHT_GRAY
        Fonts.SFApple35.drawString(module.name, x + 10, y + 12, textColor.rgb)
        
        if (expanded) {
            RenderUtils.drawRoundedRect(x, y + 40f, x + width, y + height, 6f, Color(30, 30, 35, 200).rgb)
            var valY = y + 45f
            
            for (value in module.values) {
                if (value is BoolValue) {
                    Fonts.SFApple35.drawString(value.name, x + 10, valY, Color.WHITE.rgb)
                    val boolVal = value.get()
                    RenderUtils.drawRoundedRect(x + width - 30, valY, x + width - 10, valY + 10, 4f, if (boolVal) Color.GREEN.rgb else Color.RED.rgb)
                    valY += 25f
                } else if (value is FloatValue) {
                    Fonts.SFApple35.drawString("${value.name}: ${value.get()}", x + 10, valY, Color.WHITE.rgb)
                    RenderUtils.drawRect(x + 10, valY + 12, x + width - 10, valY + 14, Color.DARK_GRAY.rgb)
                    val sliderWidth = (width - 20) * (value.get() - value.minimum) / (value.maximum - value.minimum)
                    RenderUtils.drawRect(x + 10, valY + 12, x + 10 + sliderWidth, valY + 14, Color.BLUE.rgb)
                    valY += 25f
                } else if (value is IntegerValue) {
                    Fonts.SFApple35.drawString("${value.name}: ${value.get()}", x + 10, valY, Color.WHITE.rgb)
                    RenderUtils.drawRect(x + 10, valY + 12, x + width - 10, valY + 14, Color.DARK_GRAY.rgb)
                    val sliderWidth = (width - 20) * (value.get() - value.minimum) / (value.maximum - value.minimum)
                    RenderUtils.drawRect(x + 10, valY + 12, x + 10 + sliderWidth, valY + 14, Color.BLUE.rgb)
                    valY += 25f
                } else if (value is ListValue) {
                    Fonts.SFApple35.drawString("${value.name}: ${value.get()}", x + 10, valY, Color.WHITE.rgb)
                    valY += 25f
                } else if (value is TextValue) {
                    Fonts.SFApple35.drawString("${value.name}: ${value.get()}", x + 10, valY, Color.WHITE.rgb)
                    valY += 25f
                }
            }
        }
    }
    
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int, x: Float, y: Float, width: Float, height: Float) {
        if (mouseY >= y && mouseY <= y + 40f) {
            if (mouseButton == 0) {
                module.toggle()
                Minecraft.getMinecraft().soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F))
            } else if (mouseButton == 1) {
                expanded = !expanded
                Minecraft.getMinecraft().soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F))
            }
            return
        }
        
        if (expanded) {
            var valY = y + 45f
             for (value in module.values) {
                if (value is BoolValue) {
                    if (mouseX >= x + width - 30 && mouseX <= x + width - 10 && mouseY >= valY && mouseY <= valY + 10) {
                        value.set(!value.get())
                        Minecraft.getMinecraft().soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F))
                    }
                    valY += 25f
                } else if (value is FloatValue) {
                    if (mouseX >= x + 10 && mouseX <= x + width - 10 && mouseY >= valY + 10 && mouseY <= valY + 16) {
                        val percent = (mouseX - (x + 10)) / (width - 20)
                        val `val` = value.minimum + (value.maximum - value.minimum) * percent
                        value.set(`val`)
                    }
                    valY += 25f
                } else if (value is IntegerValue) {
                    if (mouseX >= x + 10 && mouseX <= x + width - 10 && mouseY >= valY + 10 && mouseY <= valY + 16) {
                        val percent = (mouseX - (x + 10)) / (width - 20)
                        val `val` = value.minimum + (value.maximum - value.minimum) * percent
                        value.set(`val`.toInt())
                    }
                    valY += 25f
                } else if (value is ListValue) {
                    if (mouseX >= x && mouseX <= x + width && mouseY >= valY && mouseY <= valY + 20) {
                         val values = value.values
                         var index = values.indexOf(value.get())
                         index++
                         if (index >= values.size) index = 0
                         value.set(values[index])
                         Minecraft.getMinecraft().soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 1.0F))
                    }
                    valY += 25f
                } else if (value is TextValue) {
                    valY += 25f
                }
            }
        }
    }
}
