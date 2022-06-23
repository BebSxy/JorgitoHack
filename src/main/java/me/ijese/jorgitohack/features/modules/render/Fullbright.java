// 
// Decompiled by Procyon v0.5.36
// 

package me.ijese.jorgitohack.features.modules.render;

import me.ijese.jorgitohack.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketEntityEffect;
import me.ijese.jorgitohack.event.events.PacketEvent;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import me.ijese.jorgitohack.features.modules.Module;

public class Fullbright extends Module
{
    public Setting<Mode> mode;
    public Setting<Boolean> effects;
    private float previousSetting;

    public Fullbright() {
        super("Fullbright", "Makes your game brighter.", Category.RENDER, true, false, false);
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.GAMMA));
        this.effects = (Setting<Boolean>)this.register(new Setting("Effects", false));
        this.previousSetting = 1.0f;
    }

    @Override
    public void onEnable() {
        this.previousSetting = Fullbright.mc.gameSettings.gammaSetting;
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.GAMMA) {
            Fullbright.mc.gameSettings.gammaSetting = 1000.0f;
        }
        if (this.mode.getValue() == Mode.POTION) {
            Fullbright.mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() == Mode.POTION) {
            Fullbright.mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        Fullbright.mc.gameSettings.gammaSetting = this.previousSetting;
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && event.getPacket() instanceof SPacketEntityEffect && this.effects.getValue()) {
            final SPacketEntityEffect packet = event.getPacket();
            if (Fullbright.mc.player != null && packet.getEntityId() == Fullbright.mc.player.getEntityId() && (packet.getEffectId() == 9 || packet.getEffectId() == 15)) {
                event.setCanceled(true);
            }
        }
    }

    public enum Mode
    {
        GAMMA,
        POTION;
    }
}