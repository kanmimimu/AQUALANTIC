package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.ccbluex.liquidbounce.features.module.modules.visual.Animations;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public int heldItemRight;

    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At("HEAD"))
    private void onSetRotationAnglesHead(float p1, float p2, float p3, float p4, float p5, float p6, Entity entity,
            CallbackInfo ci) {
        if (entity instanceof EntityPlayer && entity.equals(Minecraft.getMinecraft().thePlayer)) {
            if (heldItemRight != 0 && heldItemRight != 3 && Animations.isBlockingForRender()) {
                this.heldItemRight = 3;
            }
        }
    }

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float p1, float p2, float p3, float p4, float p5, float p6, Entity entity,
            CallbackInfo ci) {
        if (heldItemRight == 3 && Animations.INSTANCE.getOldAnimations().get()) {
            this.bipedRightArm.rotateAngleY = 0F;
        }

        if (RotationUtils.serverRotation != null && entity instanceof EntityPlayer
                && entity.equals(Minecraft.getMinecraft().thePlayer)) {
            float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
            this.bipedHead.rotateAngleX = (float) Math.toRadians(
                    RenderUtils.interpolate(RotationUtils.headPitch, RotationUtils.prevHeadPitch, partialTicks));
        }
    }
}