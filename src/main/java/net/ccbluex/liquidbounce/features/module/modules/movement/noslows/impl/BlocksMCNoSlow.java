package net.ccbluex.liquidbounce.features.module.modules.movement.noslows.impl;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.noslows.NoSlowMode;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {1, 6, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000b"},
        d2 = {"Lnet/ccbluex/liquidbounce/features/module/modules/movement/noslows/impl/BlocksMCNoSlow;", "Lnet/ccbluex/liquidbounce/features/module/modules/movement/noslows/NoSlowMode;", "()V", "tickCycle", "", "onPreMotion", "", "event", "Lnet/ccbluex/liquidbounce/event/MotionEvent;", "slow", "", "CrossSine"}
)
public final class BlocksMCNoSlow extends NoSlowMode {
    private int tickCycle;

    public BlocksMCNoSlow() {
        super("BlocksMC");
    }

    public void onPreMotion(@NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        if ((this.getHoldConsume() || this.getHoldBow()) && MinecraftInstance.mc.thePlayer.isUsingItem()) {
            this.tickCycle++;
            if (this.tickCycle == 1) {
                PacketUtils.sendPacketNoEvent((Packet)(new C09PacketHeldItemChange((MinecraftInstance.mc.thePlayer.inventory.currentItem + 1) % 9)));
                PacketUtils.sendPacketNoEvent((Packet)(new C09PacketHeldItemChange(MinecraftInstance.mc.thePlayer.inventory.currentItem)));
                PacketUtils.sendPacketNoEvent((Packet)(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 0, MinecraftInstance.mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F)));
            }
        } else {
            this.tickCycle = 0;
        }

    }

    public float slow() {
        return 1.0F;
    }
}
