//
// Decompiled by Procyon v0.5.36
//

package me.ijese.jorgitohack.mixin.mixins;

import me.ijese.jorgitohack.event.events.MoveEvent;
import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.modules.misc.BetterPortals;
import me.ijese.jorgitohack.features.modules.movement.Sprint;
import net.minecraft.entity.MoverType;
import me.ijese.jorgitohack.event.events.UpdateWalkingPlayerEvent;
import me.ijese.jorgitohack.event.events.PushEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.ijese.jorgitohack.util.Util;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import me.ijese.jorgitohack.event.events.ChatEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.entity.AbstractClientPlayer;

@Mixin(value = { EntityPlayerSP.class }, priority = 9998)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    public MixinEntityPlayerSP(final Minecraft p_i47378_1_, final World p_i47378_2_, final NetHandlerPlayClient p_i47378_3_, final StatisticsManager p_i47378_4_, final RecipeBook p_i47378_5_) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
    }

    @Inject(method = { "sendChatMessage" }, at = { @At("HEAD") }, cancellable = true)
    public void sendChatMessage(final String message, final CallbackInfo callback) {
        final ChatEvent chatEvent = new ChatEvent(message);
        MinecraftForge.EVENT_BUS.post((Event)chatEvent);
    }

    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;closeScreen()V"))
    public void closeScreenHook(final EntityPlayerSP entityPlayerSP) {
        if (!BetterPortals.getInstance().isOn() || !BetterPortals.getInstance().portalChat.getValue()) {
            entityPlayerSP.closeScreen();
        }
    }


    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    public void displayGuiScreenHook(final Minecraft mc, final GuiScreen screen) {
        if (!BetterPortals.getInstance().isOn() || !BetterPortals.getInstance().portalChat.getValue()) {
            mc.displayGuiScreen(screen);
        }
    }

    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(final EntityPlayerSP entityPlayerSP, final boolean sprinting) {
        if (Sprint.getInstance().isOn() && Sprint.getInstance().mode.getValue() == Sprint.Mode.RAGE && (Util.mc.player.movementInput.moveForward != 0.0f || Util.mc.player.movementInput.moveStrafe != 0.0f)) {
            entityPlayerSP.setSprinting(true);
        }
        else {
            entityPlayerSP.setSprinting(sprinting);
        }
    }

    @Inject(method = { "pushOutOfBlocks" }, at = { @At("HEAD") }, cancellable = true)
    private void pushOutOfBlocksHook(final double x, final double y, final double z, final CallbackInfoReturnable<Boolean> info) {
        final PushEvent event = new PushEvent(1);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("HEAD") })
    private void preMotion(final CallbackInfo info) {
        final UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("RETURN") })
    private void postMotion(final CallbackInfo info) {
        final UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    @Inject(method = { "Lnet/minecraft/client/entity/EntityPlayerSP;setServerBrand(Ljava/lang/String;)V" }, at = { @At("HEAD") })
    public void getBrand(final String brand, final CallbackInfo callbackInfo) {
        if (JorgitoHack.serverManager != null) {
            JorgitoHack.serverManager.setServerBrand(brand);
        }
    }

    @Redirect(method = { "move" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(final AbstractClientPlayer player, final MoverType moverType, final double x, final double y, final double z) {
        final MoveEvent event = new MoveEvent(0, moverType, x, y, z);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            super.move(event.getType(), event.getX(), event.getY(), event.getZ());
        }
    }
}