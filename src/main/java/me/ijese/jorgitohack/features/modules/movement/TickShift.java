package me.ijese.jorgitohack.features.modules.movement;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.EntityUtil;
import net.minecraft.entity.Entity;

public class TickShift extends Module
{
    Setting<Integer> ticksVal;
    Setting<Float> timer;
    boolean canTimer;
    int tick;

    public TickShift() {
        super("TickShift", "Makes you go Faster", Module.Category.MOVEMENT, true, false, false);
        this.ticksVal = (Setting<Integer>)this.register(new Setting("Ticks", 18, 1, 100));
        this.timer = (Setting<Float>)this.register(new Setting("Timer", 1.8f, 1.0f, 3.0f));
        this.canTimer = false;
        this.tick = 0;
    }

    public void onEnable() {
        this.canTimer = false;
        this.tick = 0;
    }

    public void onUpdate() {
        if (this.tick <= 0) {
            this.tick = 0;
            this.canTimer = false;
            TickShift.mc.timer.tickLength = 50.0f;
        }
        if (this.tick > 0 && EntityUtil.isEntityMoving((Entity)TickShift.mc.player)) {
            --this.tick;
            TickShift.mc.timer.tickLength = 50.0f / this.timer.getValue();
        }
        if (!EntityUtil.isEntityMoving((Entity)TickShift.mc.player)) {
            ++this.tick;
        }
        if (this.tick >= this.ticksVal.getValue()) {
            this.tick = this.ticksVal.getValue();
        }
    }

    public String getDisplayInfo() {
        return String.valueOf(this.tick);
    }

    public void onDisable() {
        TickShift.mc.timer.tickLength = 50.0f;
    }
}