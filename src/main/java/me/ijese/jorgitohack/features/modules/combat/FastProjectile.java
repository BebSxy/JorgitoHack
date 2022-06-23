package me.ijese.jorgitohack.features.modules.combat;

import me.ijese.jorgitohack.event.events.PacketEvent;
import me.ijese.jorgitohack.features.command.Command;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastProjectile
        extends Module {
    public Setting<Boolean> Bows = this.register(new Setting<Boolean>("Bows", true));
    public Setting<Boolean> pearls = this.register(new Setting<Boolean>("Pearls", true));
    public Setting<Boolean> eggs = this.register(new Setting<Boolean>("Eggs", true));
    public Setting<Boolean> snowballs = this.register(new Setting<Boolean>("SnowBallz", true));
    public Setting<Integer> Timeout = this.register(new Setting<Integer>("Timeout", 5000, 100, 20000));
    public Setting<Integer> spoofs = this.register(new Setting<Integer>("Spoofs", 10, 1, 300));
    public Setting<Boolean> bypass = this.register(new Setting<Boolean>("Bypass", false));
    public Setting<Boolean> debug = this.register(new Setting<Boolean>("Debug", false));
    private boolean shooting;
    private long lastShootTime;

    public FastProjectile() {
        super("BowHack", "wtffff.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (this.isEnabled()) {
            this.shooting = false;
            this.lastShootTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        ItemStack handStack;
        CPacketPlayerTryUseItem packet2;
        if (event.getStage() != 0) {
            return;
        }
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            ItemStack handStack2;
            CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && !(handStack2 = FastProjectile.mc.player.getHeldItem(EnumHand.MAIN_HAND)).isEmpty() && handStack2.getItem() != null && handStack2.getItem() instanceof ItemBow && this.Bows.getValue().booleanValue()) {
                this.doSpoofs();
                if (this.debug.getValue().booleanValue()) {
                    Command.sendMessage("trying to spoof");
                }
            }
        } else if (event.getPacket() instanceof CPacketPlayerTryUseItem && (packet2 = (CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.MAIN_HAND && !(handStack = FastProjectile.mc.player.getHeldItem(EnumHand.MAIN_HAND)).isEmpty() && handStack.getItem() != null) {
            if (handStack.getItem() instanceof ItemEgg && this.eggs.getValue().booleanValue()) {
                this.doSpoofs();
            } else if (handStack.getItem() instanceof ItemEnderPearl && this.pearls.getValue().booleanValue()) {
                this.doSpoofs();
            } else if (handStack.getItem() instanceof ItemSnowball && this.snowballs.getValue().booleanValue()) {
                this.doSpoofs();
            }
        }
    }

    private void doSpoofs() {
        if (System.currentTimeMillis() - this.lastShootTime >= (long)this.Timeout.getValue().intValue()) {
            this.shooting = true;
            this.lastShootTime = System.currentTimeMillis();
            FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) FastProjectile.mc.player, CPacketEntityAction.Action.START_SPRINTING));
            for (int index = 0; index < this.spoofs.getValue(); ++index) {
                if (this.bypass.getValue().booleanValue()) {
                    FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX, FastProjectile.mc.player.posY + 1.0E-10, FastProjectile.mc.player.posZ, false));
                    FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX, FastProjectile.mc.player.posY - 1.0E-10, FastProjectile.mc.player.posZ, true));
                    continue;
                }
                FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX, FastProjectile.mc.player.posY - 1.0E-10, FastProjectile.mc.player.posZ, true));
                FastProjectile.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(FastProjectile.mc.player.posX, FastProjectile.mc.player.posY + 1.0E-10, FastProjectile.mc.player.posZ, false));
            }
            if (this.debug.getValue().booleanValue()) {
                Command.sendMessage("Spoofed");
            }
            this.shooting = false;
        }
    }
}

