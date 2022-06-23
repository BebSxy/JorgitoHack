package me.ijese.jorgitohack;

import me.ijese.jorgitohack.manager.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = "jorgitohack", name = "JorgitoHack", version = "1.0")
public class JorgitoHack {
    public static final String MODID = "jorgitohack";
    public static final String MODNAME = "JorgitoHack";
    public static final String MODUNICODE = "\u200e\ufe0f\u200d\ud83d\udd25";
    public static final String MODVER = "1.0";
    public static final Logger LOGGER = LogManager.getLogger("JorgitoHack v1.0");
    public static PotionManager potionManager;
    public static TotemPopManager totemPopManager;
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static SafetyManager safetyManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    @Mod.Instance
    public static JorgitoHack INSTANCE;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        LOGGER.info("\nLoading JorgitoHack");
        unloaded = false;
        if (reloadManager != null) {
            reloadManager.unload();
            reloadManager = null;
        }
        totemPopManager = new TotemPopManager();
        timerManager = new TimerManager();
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        rotationManager = new RotationManager();
        packetManager = new PacketManager();
        eventManager = new EventManager();
        speedManager = new SpeedManager();
        potionManager = new PotionManager();
        inventoryManager = new InventoryManager();
        serverManager = new ServerManager();
        fileManager = new FileManager();
        safetyManager = new SafetyManager ( );
        colorManager = new ColorManager();
        positionManager = new PositionManager();
        configManager = new ConfigManager();
        holeManager = new HoleManager();
        LOGGER.info("Manager loaded.");
        moduleManager.init();
        LOGGER.info("Modules loaded.");
        configManager.init();
        eventManager.init();
        LOGGER.info("EventManager loaded.");
        textManager.init(true);
        moduleManager.onLoad();
        LOGGER.info("JorgitoHack successfully loaded!");
    }

    public static void unload(boolean unload) {
        LOGGER.info("\n\nUnloading JorgitoHack by iJese");
        if (unload) {
            reloadManager = new ReloadManager();
            reloadManager.init(commandManager != null ? commandManager.getPrefix() : ".");
        }
        JorgitoHack.onUnload();
        potionManager = null;
        timerManager = null;
        eventManager = null;
        friendManager = null;
        speedManager = null;
        holeManager = null;
        positionManager = null;
        rotationManager = null;
        configManager = null;
        safetyManager = null;
        commandManager = null;
        colorManager = null;
        serverManager = null;
        fileManager = null;
        inventoryManager = null;
        moduleManager = null;
        textManager = null;
        LOGGER.info("JorgitoHack unloaded!\n");
    }

    public static void reload() {
        JorgitoHack.unload(false);
        JorgitoHack.load();
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(JorgitoHack.configManager.config.replaceFirst("jorgitohack/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Primooctopus33 was here");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("JorgitoHack");
        JorgitoHack.load();
    }
}

