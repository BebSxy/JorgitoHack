package me.ijese.jorgitohack.features.modules.player;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import net.minecraft.util.math.MathHelper;

public class TimerSpeed extends Module {

    public TimerSpeed() {
        super("Timer", "Allows you to change the client ticks per second", Category.PLAYER, true, false, false);
    }

    public final Setting<Boolean> tpsSync = this.register(new Setting("TpsSync", false));
    public final Setting<Float> multiplier = this.register(new Setting("Multiplier", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    private static float[] tickRates = new float[20];

    public void onUpdate() {
        mc.timer.tickLength = 50f / getMultiplier();
    }

    public void onDisable() {
        mc.timer.tickLength = 50f;
    }

    public float getMultiplier() {
        if (this.isEnabled()) {
            if (tpsSync.getValue()) {
                float f = getTickRate() / 20 * (float) multiplier.getValue().floatValue();
                if (f < 0.1f) f = 0.1f;
                return f;
            } else {
                return (float) multiplier.getValue().floatValue();
            }
        } else {
            return 1.0f;
        }
    }

    public static float getTickRate() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks += 1.0F;
            }
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.0F, 20.0F);
    }

}