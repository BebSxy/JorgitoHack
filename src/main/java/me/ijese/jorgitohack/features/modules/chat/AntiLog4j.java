package me.ijese.jorgitohack.features.modules.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.command.Command;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.event.events.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class AntiLog4j
        extends Module {
    public AntiLog4j() {
        super("AntiLog4j", "Stops you from registering text containing what the log4j exploit text would contain", Module.Category.CHAT, true, false, false);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onPacketRecieve(PacketEvent.Receive event) {
        String text;
        if (event.getPacket() instanceof SPacketChat && ((text = ((SPacketChat)event.getPacket()).getChatComponent().getUnformattedText()).contains("${") || text.contains("$<") || text.contains("$:-") || text.contains("jndi:ldap"))) {
            event.setCanceled(true);
        }
    }

    public static class PearlNotify extends Module {
        private final HashMap<EntityPlayer, UUID> list;
        private Entity enderPearl;
        private boolean flag;

        public PearlNotify() {
            super("PearlNotify", "Notify pearl throws.", Category.CHAT, true, false, false);
            this.list = new HashMap<EntityPlayer, UUID>();
        }

        @Override
        public void onEnable() {
            this.flag = true;
        }

        @Override
        public void onUpdate() {
            if (PearlNotify.mc.world == null || PearlNotify.mc.player == null) {
                return;
            }
            this.enderPearl = null;
            for (final Entity e : PearlNotify.mc.world.loadedEntityList) {
                if (e instanceof EntityEnderPearl) {
                    this.enderPearl = e;
                    break;
                }
            }
            if (this.enderPearl == null) {
                this.flag = true;
                return;
            }
            EntityPlayer closestPlayer = null;
            for (final EntityPlayer entity : PearlNotify.mc.world.playerEntities) {
                if (closestPlayer == null) {
                    closestPlayer = entity;
                } else {
                    if (closestPlayer.getDistance(this.enderPearl) <= entity.getDistance(this.enderPearl)) {
                        continue;
                    }
                    closestPlayer = entity;
                }
            }
            if (closestPlayer == PearlNotify.mc.player) {
                this.flag = false;
            }
            if (closestPlayer != null && this.flag) {
                String faceing = this.enderPearl.getHorizontalFacing().toString();
                if (faceing.equals("west")) {
                    faceing = "east";
                } else if (faceing.equals("east")) {
                    faceing = "west";
                }
                Command.sendMessage(JorgitoHack.friendManager.isFriend(closestPlayer.getName()) ? (ChatFormatting.AQUA + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!") : (ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!"));
                this.flag = false;
            }
        }
    }
}

