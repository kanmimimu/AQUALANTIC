package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.ui.client.gui.clickgui.style.styles.material3.M3Colors;
import net.ccbluex.liquidbounce.ui.client.gui.m3.M3Button;
import net.ccbluex.liquidbounce.ui.client.gui.m3.M3ButtonStyle;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends GuiScreen {

    private List<M3Button> m3Buttons;
    private float openProgress = 0f;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        m3Buttons = new ArrayList<>();

        float buttonWidth = 160f;
        float buttonHeight = 40f;
        float spacing = 12f;
        float startY = this.height / 2f - 60f;
        float centerX = this.width / 2f - buttonWidth / 2f;

        m3Buttons.add(new M3Button(1, centerX, startY, buttonWidth, buttonHeight,
                I18n.format("menu.returnToGame"), M3ButtonStyle.FILLED_TONAL));
        m3Buttons.add(new M3Button(4, centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight,
                I18n.format("menu.options"), M3ButtonStyle.FILLED_TONAL));

        if (this.mc.isSingleplayer()) {
            m3Buttons.add(new M3Button(5, centerX, startY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight,
                    I18n.format("menu.shareToLan"), M3ButtonStyle.FILLED_TONAL));
        } else {
            m3Buttons.add(new M3Button(6, centerX, startY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight,
                    "Server List", M3ButtonStyle.FILLED_TONAL));
        }

        m3Buttons.add(new M3Button(7, centerX, startY + (buttonHeight + spacing) * 3, buttonWidth, buttonHeight,
                this.mc.isSingleplayer() ? I18n.format("menu.returnToMenu") : I18n.format("menu.disconnect"),
                M3ButtonStyle.FILLED_TONAL));

        this.buttonList.clear();
        openProgress = 0f;
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        callbackInfo.cancel();

        float deltaTime = Math.max(1f, Math.min(50f, RenderUtils.deltaTime));
        openProgress = Math.min(1f, openProgress + 0.008f * deltaTime);
        float easedProgress = easeOutQuart(openProgress);

        Color bgColor = new Color(
                M3Colors.INSTANCE.getSurfaceContainerLow().getRed(),
                M3Colors.INSTANCE.getSurfaceContainerLow().getGreen(),
                M3Colors.INSTANCE.getSurfaceContainerLow().getBlue(),
                (int) (180 * easedProgress));
        RenderUtils.drawRect(0f, 0f, this.width, this.height, bgColor.getRGB());

        int titleAlpha = (int) (255 * easedProgress);
        float titleY = this.height / 2f - 100f - (1f - easedProgress) * 20f;

        Fonts.font40.drawCenteredString(
                "Game Menu",
                this.width / 2f,
                titleY,
                M3Colors.INSTANCE.withAlpha(M3Colors.INSTANCE.getOnSurface(), titleAlpha).getRGB(),
                false);

        for (int i = 0; i < m3Buttons.size(); i++) {
            M3Button button = m3Buttons.get(i);
            float slideOffset = (1f - easedProgress) * (30f + i * 8f);
            float originalY = button.getY();
            button.setY(originalY + slideOffset);
            button.draw(mouseX, mouseY, partialTicks);
            button.setY(originalY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (M3Button button : m3Buttons) {
                if (button.mousePressed(mouseX, mouseY, mouseButton)) {
                    break;
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (M3Button button : m3Buttons) {
            if (button.mouseReleased(mouseX, mouseY, state)) {
                handleM3ButtonClick(button.getId());
                break;
            }
        }
    }

    private void handleM3ButtonClick(int id) {
        switch (id) {
            case 1:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;
            case 4:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 5:
                this.mc.displayGuiScreen(new GuiShareToLan(this));
                break;
            case 6:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 7:
                boolean singleplayer = this.mc.isSingleplayer();
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient) null);
                this.mc.displayGuiScreen(singleplayer ? new GuiMainMenu() : new GuiMultiplayer(new GuiMainMenu()));
                break;
        }
    }

    private float easeOutQuart(float x) {
        float t = 1f - x;
        return 1f - t * t * t * t;
    }
}
