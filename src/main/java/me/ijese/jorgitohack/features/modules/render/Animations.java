package me.ijese.jorgitohack.features.modules.render;

import me.ijese.jorgitohack.event.events.Render3DEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;

    public class Animations extends Module {

        public Setting<Boolean> playersDisableAnimations = register(new Setting("DisableAnimations", true));
        public Setting<Boolean> changeMainhand = register(new Setting("ChangeMainhand", false));
        public Setting<Double> mainhand = register (new Setting("MainHand", 1.0d, 0.0d, 1.0d));
        public Setting<Boolean> changeOffhand = register(new Setting("ChangeOffhand", false));
        public Setting<Double> offhand = register(new Setting("OffHand", 1.0d, 0.0d, 1.0d));
        public Setting<Boolean> changeSwing = register(new Setting("ChangeSwing", true));
        public Setting<Integer> swingDelay = register(new Setting("Delay", 6, 1, 30));

        public static Animations INSTANCE;

        public Animations() {
            super("Animations", "lol", Category.RENDER, true, false, false);
            INSTANCE = this;
        }

        @Override
        public void onRender3D(Render3DEvent event) {
            if (playersDisableAnimations.getValue()) {
                for (EntityPlayer player : Animations.mc.world.playerEntities) {
                    player.limbSwing = 0f;
                    player.limbSwingAmount = 0f;
                    player.prevLimbSwingAmount = 0f;
                }
            }
            if (changeMainhand.getValue() && mc.itemRenderer.equippedProgressMainHand != mainhand.getValue().floatValue()) {
                mc.itemRenderer.equippedProgressMainHand = mainhand.getValue().floatValue();
                mc.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
            if (changeOffhand.getValue() && mc.itemRenderer.equippedProgressOffHand != offhand.getValue().floatValue()) {
                mc.itemRenderer.equippedProgressOffHand = offhand.getValue().floatValue();
                mc.itemRenderer.itemStackOffHand = mc.player.getHeldItemOffhand();
            }
        }
    }
