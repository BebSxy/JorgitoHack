package me.ijese.jorgitohack.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.event.events.ClientEvent;
import me.ijese.jorgitohack.event.events.Render2DEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.modules.misc.ToolTips;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.manager.TextManager;
import me.ijese.jorgitohack.util.*;
import me.ijese.jorgitohack.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class HUD extends Module {
    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static HUD INSTANCE = new HUD();
    public Setting<String> gameTitle = register(new Setting("AppTitle", "JorgitoHack v1.0"));
    private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
    private final Setting<Boolean> waterMark = register(new Setting("Watermark", Boolean.valueOf(false), "displays watermark"));
    private final Setting<String> waterMarkName = register(new Setting("WaterMarkName", "JorgitoHack v1.0", v -> this.waterMark.getValue()));
    public final Setting<Boolean> arrayList = register(new Setting("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
    public final Setting<Boolean> flowingArrayList = this.register(new Setting<Boolean>("Flowing ArrayList", true));
    private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(false), "Your current coordinates"));
    private final Setting<Boolean> direction = register(new Setting("Direction", Boolean.valueOf(false), "The Direction you are facing."));
    private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false), "ArmorHUD"));
    private final Setting<Boolean> totems = register(new Setting("Totems", Boolean.valueOf(false), "TotemHUD"));
    public Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(false), "Your Speed"));
    public Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", Boolean.valueOf(false), "Active potion effects"));
    public Setting<Boolean> potionSync = this.register(new Setting<Boolean>("PotionSync", Boolean.valueOf(false), v -> this.potions.getValue()));
    private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(false), "Your response time to the server."));
    private final Setting<Boolean> ms = register(new Setting("Ms", false, v -> this.ping.getValue()));
    private final Setting<Boolean> tps = register(new Setting("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
    private final Setting<Boolean> fps = register(new Setting("FPS", Boolean.valueOf(false), "Your frames per second."));
    private final Setting<Boolean> server = register(new Setting("Server", false, "Shows the server"));
    public Setting < Boolean > oneDot15 = this.register ( new Setting <> ( "1.15" , false ) );
    private final Setting<Greeter> greeter = this.register(new Setting<Greeter>("Greeter", Greeter.NONE, "Greets you."));
    public Setting<Boolean> shadow = this.register(new Setting<Boolean>("Shadow", Boolean.valueOf(false), "Draws the text with a shadow."));
    private final Setting<String> spoofGreeter = this.register(new Setting<Object>("GreeterName", "iJese", v -> this.greeter.getValue() == Greeter.CUSTOM));
    private final Setting<Boolean> lag = register(new Setting("LagNotifier", Boolean.valueOf(false), "The time"));
    public Setting<String> command = register(new Setting("Command", "JorgitoHack+"));
    public Setting<TextUtil.Color> bracketColor = register(new Setting("BracketColor", TextUtil.Color.BLUE));
    public Setting<TextUtil.Color> commandColor = register(new Setting("NameColor", TextUtil.Color.BLUE));
    public Setting<Boolean> rainbowPrefix = this.register(new Setting<Boolean>("RainbowPrefix", false));
    public Setting<Integer> rainbowSpeed = this.register(new Setting<Object>("PrefixSpeed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> this.rainbowPrefix.getValue()));
    public Setting<String> commandBracket = register(new Setting("Bracket", "("));
    public Setting<String> commandBracket2 = register(new Setting("Bracket2", ")"));
    public Setting<Boolean> notifyToggles = register(new Setting("ChatNotify", Boolean.valueOf(false), "notifys in chat"));
    private final Timer timer = new Timer();
    private Map<String, Integer> players = new HashMap<>();
    public Setting<Integer> animationHorizontalTime = register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> this.arrayList.getValue().booleanValue()));
    public Setting<Integer> animationVerticalTime = register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> this.arrayList.getValue().booleanValue()));
    public Setting<RenderingMode> renderingMode = register(new Setting("Ordering", RenderingMode.ABC));
    public Setting<Integer> waterMarkY = register(new Setting("WatermarkPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> this.waterMark.getValue().booleanValue()));
    public Setting<Boolean> textRadar = this.register(new Setting<Boolean>("TextRadar", Boolean.valueOf(false), "A TextRadar"));
    public Setting<Integer> textRadarUpdates = this.register(new Setting<Integer>("TRUpdates", 500, 0, 1000));
    public Setting<Boolean> time = register(new Setting("Time", Boolean.valueOf(false), "The time"));
    public Setting<Integer> lagTime = register(new Setting("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000)));
    public Setting<Boolean> colorSync = this.register(new Setting("Sync", Boolean.valueOf(false), "Universal colors for hud."));
    public Setting<Boolean> safety = this.register(new Setting<Boolean>("SafetyPlayer", false));
    public Setting<Integer> safetyCheck = this.register(new Setting<Integer>("SafetyCheck", 50, 1, 150));
    public Setting<Integer> safetySync = this.register(new Setting<Integer>("SafetySync", 250, 1, 10000));
    public Setting<Boolean> rolling = this.register(new Setting<Object>("Rolling", Boolean.valueOf(false), v -> this.rainbow.getValue()));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", Boolean.valueOf(false), "Rainbow hud."));
    public Setting<Boolean> oneChunk = this.register(new Setting<Boolean>("OneChunk", false));
    private final Setting<Boolean> grayNess = register(new Setting("Gray", Boolean.valueOf(true)));
    public Map<Integer, Integer> colorHeightMap = new HashMap<Integer, Integer>();
    public Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    public float hue;
    private Setting holeThread;


    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
        setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null)
            INSTANCE = new HUD();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (this.shouldIncrement)
            this.hitMarkerTimer++;
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
        if (this.timer.passedMs(HUD.getInstance().textRadarUpdates.getValue())) {
            this.players = this.getTextRadarPlayers();
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Display.setTitle(gameTitle.getValue());
        }



    public void onRender2D(Render2DEvent event) {
        if (fullNullCheck())
            return;
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        this.color = ColorUtil.toRGBA((ClickGui.getInstance()).red.getValue().intValue(), (ClickGui.getInstance()).green.getValue().intValue(), (ClickGui.getInstance()).blue.getValue().intValue());
        if (this.waterMark.getValue().booleanValue()) {
            String string = this.waterMarkName.getPlannedValue();

            if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
                if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(string, 2.0F, this.waterMarkY.getValue().intValue(), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0F;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, this.waterMarkY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                        f += this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(string, 2.0F, this.waterMarkY.getValue().intValue(), this.color, true);
            }
        }if (this.textRadar.getValue()) {
            this.drawTextRadar((ToolTips.getInstance().isOff() || !ToolTips.getInstance().shulkerSpy.getValue() || !ToolTips.getInstance().render.getValue()) ? 0 : ToolTips.getInstance().getTextRadarY());
        }
        int[] counter1 = {1};
        int j = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && !this.renderingUp.getValue().booleanValue()) ? 14 : 0;
        if (this.arrayList.getValue().booleanValue())
            if (this.renderingUp.getValue().booleanValue()) {
                if (this.renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < JorgitoHack.moduleManager.sortedModulesABC.size(); k++) {
                        String str = JorgitoHack.moduleManager.sortedModulesABC.get(k);
                        this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (2 + j * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                        j++;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < JorgitoHack.moduleManager.sortedModules.size(); k++) {
                        Module module = JorgitoHack.moduleManager.sortedModules.get(k);
                        String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                        this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (2 + j * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                        j++;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (this.renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < JorgitoHack.moduleManager.sortedModulesABC.size(); k++) {
                    String str = JorgitoHack.moduleManager.sortedModulesABC.get(k);
                    j += 10;
                    this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (height - j), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < JorgitoHack.moduleManager.sortedModules.size(); k++) {
                    Module module = JorgitoHack.moduleManager.sortedModules.get(k);
                    String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                    j += 10;
                    this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (height - j), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        String grayString = this.grayNess.getValue().booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
        int i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && this.renderingUp.getValue().booleanValue()) ? 13 : (this.renderingUp.getValue().booleanValue() ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {


            if (potions.getValue().booleanValue()) {
                List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = JorgitoHack.potionManager.getColoredPotionString(potionEffect);
                    i += 10;

                    renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (height - 2 - i), this.potionSync.getValue() ? ((ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }

            if(this.server.getValue()) {
                String sText = grayString + "Server " + ChatFormatting.WHITE + (mc.isSingleplayer() ? "SinglePlayer" :mc.getCurrentServerData().serverIP);
                i += 10;
                this.renderer.drawString(sText, (width - this.renderer.getStringWidth(sText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }

            int k = 0;
            if (this.speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + JorgitoHack.speedManager.getSpeedKpH() + " km/h";
                i += 10;
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                i += 10;
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + JorgitoHack.serverManager.getTPS();
                i += 10;
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String sText = grayString + "Server " + ChatFormatting.WHITE + (mc.isSingleplayer() ? "SinglePlayer" :mc.getCurrentServerData().serverIP);
            String str1 = grayString + "Ping " + ChatFormatting.WHITE + JorgitoHack.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {

            if (potions.getValue().booleanValue()) {
                List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = JorgitoHack.potionManager.getColoredPotionString(potionEffect);
                    renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (2 + i++ * 10), this.potionSync.getValue() ? ((ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if(this.server.getValue()) {
                String sText = grayString + "Server " + ChatFormatting.WHITE + (mc.isSingleplayer() ? "SinglePlayer" :mc.getCurrentServerData().serverIP);
                this.renderer.drawString(sText, (width - this.renderer.getStringWidth(sText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + JorgitoHack.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str = grayString + " Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + JorgitoHack.serverManager.getTPS();
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String str1 = grayString + "Ping " + ChatFormatting.WHITE + JorgitoHack.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);
        i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
        String coordinates = ChatFormatting.RESET + (inHell ? String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + hposX + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + hposZ + "]" : (String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + hposX + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + hposZ + "]"));
        String direction = this.direction.getValue().booleanValue() ? JorgitoHack.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue().booleanValue() ? coordinates : "";
        i += 10;
        if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
            String rainbowCoords = this.coords.getValue().booleanValue() ? ((inHell ? (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]") : (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]"))) : "";
            if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0F, (height - i - 11), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0F, (height - i), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            } else {
                int[] counter2 = {1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0F;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), 2.0F + s, (height - i - 11), ColorUtil.rainbow(counter2[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    s += this.renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = {1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0F;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0F + u, (height - i), ColorUtil.rainbow(counter3[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    u += this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(direction, 2.0F, (height - i - 11), this.color, true);
            this.renderer.drawString(coords, 2.0F, (height - i), this.color, true);
        }
        if (this.armor.getValue().booleanValue())
            renderArmorHUD(true);
        if (this.totems.getValue().booleanValue())
            renderTotemHUD();
        if (this.greeter.getValue() != Greeter.NONE) {
            this.renderGreeter();
        }
        if (this.lag.getValue().booleanValue())
            renderLag();
    }

    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        final int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.greeter.getValue()) {
            case TIME: {
                text = text + MathUtil.getTimeOfDay() + HUD.mc.player.getDisplayNameString();
                break;
            }
            case CHRISTMAS: {
                text = text + "Merry Christmas " + HUD.mc.player.getDisplayNameString() + " :^)";
                break;
            }
            case LONG: {
                text = text + "Welcome to JorgitoHack " + HUD.mc.player.getDisplayNameString() + " :^)";
                break;
            }
            case CUSTOM: {
                text += this.spoofGreeter.getValue();
                break;
            }
            default: {
                text = text + "Welcome " + HUD.mc.player.getDisplayNameString();
                break;
            }
        }
        this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, (this.rolling.getValue() && this.rainbow.getValue()) ? this.colorMap.get(2) : this.color, true);
    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;
        if (JorgitoHack.serverManager.isServerNotResponding()) {
            String text = ChatFormatting.RED + "Server not responding " + MathUtil.round((float) JorgitoHack.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
            this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, this.color, true);
        }
    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int iteration = 0;
            int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            int dmg = 0;
            float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
            float red = 1.0f - green;
            if (percent) {
                dmg = 100 - (int)(red * 100.0f);
            }
            this.renderer.drawStringWithShadow(dmg + "", (float) (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toRGBA((int) (red * 255.0f), (int) (green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(AttackEntityEvent event) {
        this.shouldIncrement = true;
    }

    public void onLoad() {
        JorgitoHack.commandManager.setClientMessage(getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2) {
            if (this.oneChunk.getPlannedValue().booleanValue()) {
                HUD.mc.gameSettings.renderDistanceChunks = 1;
            }
            if (event.getSetting() != null && this.equals(event.getSetting().getFeature())) {
                if (event.getSetting().equals(this.holeThread)) {
                    JorgitoHack.holeManager.settingChanged();
                }
                JorgitoHack.commandManager.setClientMessage(this.getCommandMessage());
            }
        }
    }


    public String getCommandMessage() {
        if (this.rainbowPrefix.getPlannedValue().booleanValue()) {
            StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
            stringBuilder.insert(0, "\u00a7+");
            stringBuilder.append("\u00a7r");
            return stringBuilder.toString();
        }
        return TextUtil.coloredString(this.commandBracket.getPlannedValue(), this.bracketColor.getPlannedValue()) + TextUtil.coloredString(this.command.getPlannedValue(), this.commandColor.getPlannedValue()) + TextUtil.coloredString(this.commandBracket2.getPlannedValue(), this.bracketColor.getPlannedValue());
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return this.commandBracket.getValue() + this.command.getValue() + this.commandBracket2.getValue();
    }

    public void drawTextRadar(final int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (final Map.Entry<String, Integer> player : this.players.entrySet()) {
                final String text = player.getKey() + " ";
                final int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, (float) y, (this.rolling.getValue() && this.rainbow.getValue()) ? this.colorMap.get(y) : this.color, true);
                y += textheight;
            }
        }
    }

    public enum Greeter {
        NONE,
        NAME,
        TIME,
        CHRISTMAS,
        LONG,
        CUSTOM
    }
    public enum RenderingMode {
        Length, ABC
    }
}