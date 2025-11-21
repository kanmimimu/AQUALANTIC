package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.ui.client.gui.ModernButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends GuiScreen {

    private List<ModernButton> modernButtons;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        modernButtons = new ArrayList<>();
        for (GuiButton button : this.buttonList) {
            ModernButton modernButton = new ModernButton(button.id, button.xPosition, button.yPosition, button.width, button.height, button.displayString);
            modernButton.setEnabled(button.enabled);
            modernButtons.add(modernButton);
        }
        this.buttonList.clear(); // Clear original buttons to prevent them from being drawn
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        callbackInfo.cancel(); // Cancel original drawScreen to take over rendering

        this.drawDefaultBackground();

        // Draw title and other elements from the original class if needed
        this.drawCenteredString(this.fontRendererObj, "Game Menu", this.width / 2, 40, 16777215);

        for (ModernButton button : modernButtons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        // We call super.drawScreen to draw other elements like labels, etc.
        // but since we cleared buttonList, it won't draw the original buttons.
        // However, GuiScreen's drawScreen is empty, so this call is not strictly necessary
        // unless other mixins are targeting it. For safety, we can leave it out.
        // super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /*
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo callbackInfo) {
        callbackInfo.cancel(); // Cancel original mouseClicked to handle it ourselves

        if (mouseButton == 0) {
            for (ModernButton button : modernButtons) {
                if (button.isHovered(mouseX, mouseY) && button.getEnabled()) {
                    // Create a temporary GuiButton to pass to the original actionPerformed
                    GuiButton originalButton = new GuiButton(button.getId(), button.getX(), button.getY(), button.getText());
                    try {
                        this.actionPerformed(originalButton);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break; // Prevent clicking multiple buttons
                }
            }
        }
    }
    */
}
