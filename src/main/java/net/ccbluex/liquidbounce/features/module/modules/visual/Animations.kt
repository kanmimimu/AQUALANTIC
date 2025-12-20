package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.FloatValue
import net.ccbluex.liquidbounce.features.value.IntegerValue
import net.ccbluex.liquidbounce.features.value.ListValue
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.SpoofItemUtils
import net.minecraft.item.ItemSword
import net.minecraft.util.MovingObjectPosition

@ModuleInfo(name = "Animations", category = ModuleCategory.VISUAL, canEnable = true, defaultOn = true, array = false)
object Animations : Module() {

    val blockingModeValue = ListValue(
        "BlockingMode",
        arrayOf(
            "1.7", "1.8", "Akrien", "Avatar", "ETB", "Exhibition", "Dortware", "Grim", "Plat",
            "Push", "Reverse", "Shield", "SigmaNew", "SigmaOld", "Slide", "SlideDown", "HSlide",
            "Moon", "Swong", "VisionFX", "Swank", "Jello", "Rotate", "Liquid", "Leaked", "Fall",
            "Yeet", "Yeet2", "None"
        ),
        "1.7"
    )

    private val showTag = BoolValue("ShowTag", false)
    private val resetValue = BoolValue("Reset", false)

    val itemPosXValue = FloatValue("ItemPosX", 0F, -1.0F, 1.0F)
    val itemPosYValue = FloatValue("ItemPosY", 0F, -1.0F, 1.0F)
    val itemPosZValue = FloatValue("ItemPosZ", 0F, -1.0F, 1.0F)
    val itemScaleValue = IntegerValue("ItemScale", 100, 0, 100)

    val swingSpeedValue = FloatValue("SwingSpeed", 1f, 0.5f, 5.0f)
    val blockSwingSpeed = FloatValue("BlockSwingSpeed", 1f, 0.5f, 5.0f)

    val blockItemPosX = FloatValue("BlockItemPosX", 0F, -1.0F, 1.0F)
    val blockItemPosY = FloatValue("BlockItemPosY", 0F, -1.0F, 1.0F)
    val blockItemPosZ = FloatValue("BlockItemPosZ", 0F, -1.0F, 1.0F)

    val fluxAnimation = BoolValue("Flux Swing", false)
    val anythingBlock = BoolValue("AnythingBlock", false)

    val fakeBlock = BoolValue("FakeBlock", false)
    val fakeBlockOnlySwing = BoolValue("FakeBlockOnlySwing", false).displayable { fakeBlock.get() } as BoolValue

    val oldAnimations = BoolValue("OldAnimations", false)

    override val tag: String?
        get() = if (showTag.get()) blockingModeValue.get() else null

    @JvmStatic
    fun isFakeBlockActive(): Boolean {
        if (!state || !fakeBlock.get()) return false
        val player = mc.thePlayer ?: return false
        val displayItem = SpoofItemUtils.getStack() ?: return false
        if (displayItem.item !is ItemSword) return false
        return if (fakeBlockOnlySwing.get()) player.isSwingInProgress else true
    }

    @JvmStatic
    fun isBlockingForRender(): Boolean {
        val player = mc.thePlayer ?: return false
        return player.isBlocking || KillAura.displayBlocking || isFakeBlockActive()
    }

    @JvmStatic
    fun getEffectiveSwingSpeed(): Float {
        return if (isBlockingForRender()) blockSwingSpeed.get() else swingSpeedValue.get()
    }

    fun getCustomItemPosX(): Float {
        return if (isBlockingForRender()) blockItemPosX.get() else itemPosXValue.get()
    }

    fun getCustomItemPosY(): Float {
        return if (isBlockingForRender()) blockItemPosY.get() else itemPosYValue.get()
    }

    fun getCustomItemPosZ(): Float {
        return if (isBlockingForRender()) blockItemPosZ.get() else itemPosZValue.get()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (oldAnimations.get()) {
            mc.thePlayer.isSneaking = true
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (resetValue.get()) {
            itemPosXValue.set(0F)
            itemPosYValue.set(0F)
            itemPosZValue.set(0F)
            itemScaleValue.set(100)
            swingSpeedValue.set(1F)
            blockSwingSpeed.set(1F)
            blockItemPosX.set(0F)
            blockItemPosY.set(0F)
            blockItemPosZ.set(0F)
            resetValue.set(false)
        }

        if (oldAnimations.get()) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown && mc.objectMouseOver?.blockPos != null) {
                mc.playerController.resetBlockRemoving()
            }
            if (mc.gameSettings.keyBindUseItem.isKeyDown &&
                mc.gameSettings.keyBindAttack.isKeyDown &&
                mc.objectMouseOver?.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
            ) {
                PlayerUtils.swing()
            }
        }
    }
}
