package me.ijese.jorgitohack.features.modules.render;

import java.awt.Color;
import me.ijese.jorgitohack.event.events.Render2DEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.modules.Module.Category;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;

public class Crosshair extends Module {
    private final Setting<Boolean> dynamic = this.register(new Setting("Dynamic", true));
    private final Setting<Float> width = this.register(new Setting("Width", 1.0F, 0.5F, 3.0F));
    private final Setting<Float> gap = this.register(new Setting("Gap", 3.0F, 0.5F, 5.0F));
    private final Setting<Float> length = this.register(new Setting("Length", 7.0F, 0.5F, 30.0F));
    private final Setting<Float> dynamicGap = this.register(new Setting("DynamicGap", 1.5F, 0.5F, 5.0F));
    private final Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
    public static Crosshair INSTANCE;

    public Crosshair() {
        super("Crosshair", "csgo be like.", Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public void onEnable() {
        GuiIngameForge.renderCrosshairs = false;
    }

    public void onDisable() {
        GuiIngameForge.renderCrosshairs = true;
    }

    public void onRender2D(Render2DEvent event) {
        int color = (new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue())).getRGB();
        ScaledResolution resolution = new ScaledResolution(mc);
        float middlex = (float)resolution.getScaledWidth() / 2.0F;
        float middley = (float)resolution.getScaledHeight() / 2.0F;
        RenderUtil.drawBordered(middlex - (Float)this.width.getValue(), middley - ((Float)this.gap.getValue() + (Float)this.length.getValue()) - (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), middlex + (Float)this.width.getValue(), middley - (Float)this.gap.getValue() - (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), 0.5F, color, -16777216);
        RenderUtil.drawBordered(middlex - (Float)this.width.getValue(), middley + (Float)this.gap.getValue() + (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), middlex + (Float)this.width.getValue(), middley + (Float)this.gap.getValue() + (Float)this.length.getValue() + (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), 0.5F, color, -16777216);
        RenderUtil.drawBordered(middlex - ((Float)this.gap.getValue() + (Float)this.length.getValue()) - (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), middley - (Float)this.width.getValue(), middlex - (Float)this.gap.getValue() - (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), middley + (Float)this.width.getValue(), 0.5F, color, -16777216);
        RenderUtil.drawBordered(middlex + (Float)this.gap.getValue() + (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), middley - (Float)this.width.getValue(), middlex + (Float)this.gap.getValue() + (Float)this.length.getValue() + (this.isMoving() && (Boolean)this.dynamic.getValue() ? (Float)this.dynamicGap.getValue() : 0.0F), middley + (Float)this.width.getValue(), 0.5F, color, -16777216);
    }

    public boolean isMoving() {
        return mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F || mc.player.moveVertical != 0.0F;
    }

    public int color(int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), hsb);
        float brightness = Math.abs((getOffset() + (float)index / (float)count * 2.0F) % 2.0F - 1.0F);
        brightness = 0.4F + 0.4F * brightness;
        hsb[2] = brightness % 1.0F;
        Color clr = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        return (new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), (Integer)this.alpha.getValue())).getRGB();
    }

    private static float getOffset() {
        return (float)(System.currentTimeMillis() % 2000L) / 1000.0F;
    }
}
