package me.ijese.jorgitohack.features.modules.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.ijese.jorgitohack.features.command.Command;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import net.minecraft.init.MobEffects;

public class PotionAlert
        extends Module {
    public final Setting<Boolean> weakness = this.register(new Setting<Boolean>("Weakness", true));
    public final Setting<Boolean> slowness = this.register(new Setting<Boolean>("Slowness", true));
    private boolean hasAnnouncedWeakness = false;
    private boolean hasAnnouncedSlowness = false;

    public PotionAlert() {
        super("PotionAlert", "Announces in chat when you have specific potion effects.", Module.Category.CHAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (this.weakness.getValue().booleanValue()) {
            if (PotionAlert.mc.player.isPotionActive(MobEffects.WEAKNESS) && !this.hasAnnouncedWeakness) {
                Command.sendMessage("You now have " + (Object)ChatFormatting.RED + (Object)ChatFormatting.BOLD + "Weakness" + (Object)ChatFormatting.RESET + "!");
                this.hasAnnouncedWeakness = true;
            }
            if (!PotionAlert.mc.player.isPotionActive(MobEffects.WEAKNESS) && this.hasAnnouncedWeakness) {
                Command.sendMessage("You no longer have " + (Object)ChatFormatting.RED + (Object)ChatFormatting.BOLD + "Weakness" + (Object)ChatFormatting.RESET + "!");
                this.hasAnnouncedWeakness = false;
            }
        }
        if (this.slowness.getValue().booleanValue()) {
            if (PotionAlert.mc.player.isPotionActive(MobEffects.SLOWNESS) && !this.hasAnnouncedSlowness) {
                Command.sendMessage("You now have " + (Object)ChatFormatting.DARK_RED + (Object)ChatFormatting.BOLD + "Slowness" + (Object)ChatFormatting.RESET + "!");
                this.hasAnnouncedSlowness = true;
            }
            if (!PotionAlert.mc.player.isPotionActive(MobEffects.SLOWNESS) && this.hasAnnouncedSlowness) {
                Command.sendMessage("You no longer have " + (Object)ChatFormatting.DARK_RED + (Object)ChatFormatting.BOLD + "Slowness" + (Object)ChatFormatting.RESET + "!");
                this.hasAnnouncedSlowness = false;
            }
        }
    }
}

