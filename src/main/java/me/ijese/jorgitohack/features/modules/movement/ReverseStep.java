package me.ijese.jorgitohack.features.modules.movement;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;

public class ReverseStep
        extends Module {
    public ReverseStep() {
        super("ReverseStep", "Screams chinese words and teleports you", Module.Category.MOVEMENT, true, false, false);
    }
    public Setting<Boolean> noLiquids = this.register(new Setting("No Liquid", true));

    @Override
    public void onUpdate() {
        if (noLiquids.getValue() && ReverseStep.mc.player.isInLava() || ReverseStep.mc.player.isInWater()) {
            return;
        }
        if (ReverseStep.mc.player.onGround) {
            ReverseStep.mc.player.motionY -= 1.0;
        }
    }
}
