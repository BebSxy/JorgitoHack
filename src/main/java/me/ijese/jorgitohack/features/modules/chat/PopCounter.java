package me.ijese.jorgitohack.features.modules.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.modules.client.HUD;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;

public class PopCounter
        extends Module {
    public static HashMap<String, Integer> TotemPopContainer = new HashMap();
    private static PopCounter INSTANCE = new PopCounter();
    private final Setting<Boolean> rainbow = register(new Setting<Boolean>("Rainbow", true));
    private final Setting<Boolean> spawnLightning = register(new Setting<Boolean>("Spawn Lighting", true));


    public PopCounter() {
        super("PopCounter", "PopCounter and my shit", Module.Category.CHAT, true, false, false);
        this.setInstance();
    }

    public static PopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopCounter();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopContainer.containsKey(player.getName())) {
            int l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.remove(player.getName());
            if (spawnLightning.getValue().booleanValue()) {
                mc.world.addWeatherEffect(new EntityLightningBolt(mc.world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), true));
            }
            if (l_Count == 1) {
                final String ov = ChatFormatting.BOLD + player.getName() + ChatFormatting.WHITE + " died after popping " + ChatFormatting.DARK_GRAY + l_Count + ChatFormatting.WHITE + " totem";
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(rainbow.getValue().booleanValue() ?  "\u00a7+" + player.getName() + " died after popping " + l_Count + " Totem!" : ov), 5936);
            } else {
                final String ov = ChatFormatting.BOLD + player.getName() + ChatFormatting.WHITE + " died after popping " + ChatFormatting.DARK_GRAY + l_Count + ChatFormatting.WHITE + " totems";
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(rainbow.getValue().booleanValue() ?  "\u00a7+" + player.getName() + " died after popping " + l_Count + " Totems!": ov), 5936);
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (PopCounter.fullNullCheck()) {
            return;
        }
        if (PopCounter.mc.player.equals(player)) {
            return;
        }
        int l_Count = 1;
        if (TotemPopContainer.containsKey(player.getName())) {
            l_Count = TotemPopContainer.get(player.getName());
            TotemPopContainer.put(player.getName(), ++l_Count);
        } else {
            TotemPopContainer.put(player.getName(), l_Count);
        }
        if (l_Count == 1) {
            final String ov = ChatFormatting.BOLD + player.getName() + ChatFormatting.WHITE + " has popped " + ChatFormatting.DARK_GRAY + l_Count + ChatFormatting.WHITE + " totem";
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(rainbow.getValue().booleanValue() ?  "\u00a7+" + player.getName() + " popped " + l_Count + " Totem." : ov), 5936);
        } else {
            final String ov = ChatFormatting.BOLD + player.getName() + ChatFormatting.WHITE + " has popped " + ChatFormatting.DARK_GRAY + l_Count + ChatFormatting.WHITE + " totems";
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(rainbow.getValue().booleanValue() ?  "\u00a7+" + player.getName() + " popped " + l_Count + " Totems." : ov), 5936);
        }
    }

}