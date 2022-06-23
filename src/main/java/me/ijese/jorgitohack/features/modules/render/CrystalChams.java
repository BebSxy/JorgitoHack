package me.ijese.jorgitohack.features.modules.render;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;

public class CrystalChams
        extends Module {
    public static CrystalChams INSTANCE;
    public Setting<modes> mode;
    public Setting<outlineModes> outlineMode;
    public Setting<Float> size;
    public Setting<Float> crystalSpeed;
    public Setting<Float> crystalBounce;
    public Setting<Boolean> enchanted;
    public Setting<Integer> enchantRed;
    public Setting<Integer> enchantGreen;
    public Setting<Integer> enchantBlue;
    public Setting<Integer> enchantAlpha;
    public Setting<Boolean> texture;
    public Setting<Boolean> colorSync;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> alpha;
    public Setting<Boolean> outline;
    public Setting<Float> lineWidth;
    public Setting<Integer> outlineRed;
    public Setting<Integer> outlineGreen;
    public Setting<Integer> outlineBlue;
    public Setting<Integer> outlineAlpha;
    public Setting<Boolean> hiddenSync;
    public Setting<Integer> hiddenRed;
    public Setting<Integer> hiddenGreen;
    public Setting<Integer> hiddenBlue;
    public Setting<Integer> hiddenAlpha;

    public CrystalChams() {
        super("CrystalChams", "Modifies crystal rendering in different ways", Category.RENDER, true, false, false);
        this.mode = (Setting<modes>)this.register(new Setting("Mode", modes.FILL));
        this.outlineMode = (Setting<outlineModes>)this.register(new Setting("Outline Mode", outlineModes.WIRE));
        this.size = (Setting<Float>)this.register(new Setting("Size", 1.0f, 0.1f, 2.0f));
        this.crystalSpeed = (Setting<Float>)this.register(new Setting("Speed", 1.0f, 0.1f, 20.0f));
        this.crystalBounce = (Setting<Float>)this.register(new Setting("Bounce", 0.2f, 0.1f, 1.0f));
        this.enchanted = (Setting<Boolean>)this.register(new Setting("Glint", false));
        this.enchantRed = (Setting<Integer>)this.register(new Setting("Glint Red", 0, 0, 255, v -> this.enchanted.getValue()));
        this.enchantGreen = (Setting<Integer>)this.register(new Setting("Glint Green", 255, 0, 255, v -> this.enchanted.getValue()));
        this.enchantBlue = (Setting<Integer>)this.register(new Setting("Glint Blue", 0, 0, 255, v -> this.enchanted.getValue()));
        this.enchantAlpha = (Setting<Integer>)this.register(new Setting("Glint Alpha", 255, 0, 255, v -> this.enchanted.getValue()));
        this.texture = (Setting<Boolean>)this.register(new Setting("Texture", false));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 0, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 0, 0, 255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", true));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getValue()));
        this.outlineRed = (Setting<Integer>)this.register(new Setting("Outline Red", 0, 0, 255, v -> this.outline.getValue()));
        this.outlineGreen = (Setting<Integer>)this.register(new Setting("Outline Green", 255, 0, 255, v -> this.outline.getValue()));
        this.outlineBlue = (Setting<Integer>)this.register(new Setting("Outline Blue", 0, 0, 255, v -> this.outline.getValue()));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("Outline Alpha", 255, 0, 255, v -> this.outline.getValue()));
        this.hiddenSync = (Setting<Boolean>)this.register(new Setting("Hidden Sync", false));
        this.hiddenRed = (Setting<Integer>)this.register(new Setting("Hidden Red", 255, 0, 255, v -> !this.hiddenSync.getValue()));
        this.hiddenGreen = (Setting<Integer>)this.register(new Setting("Hidden Green", 0, 0, 255, v -> !this.hiddenSync.getValue()));
        this.hiddenBlue = (Setting<Integer>)this.register(new Setting("Hidden Blue", 0, 0, 255, v -> !this.hiddenSync.getValue()));
        this.hiddenAlpha = (Setting<Integer>)this.register(new Setting("Hidden Alpha", 255, 0, 255, v -> !this.hiddenSync.getValue()));
        CrystalChams.INSTANCE = this;
    }

    public enum outlineModes
    {
        WIRE,
        FLAT;
    }

    public enum modes
    {
        FILL,
        WIREFRAME;
    }
}