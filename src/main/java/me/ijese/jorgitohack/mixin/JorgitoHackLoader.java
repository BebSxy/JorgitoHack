package me.ijese.jorgitohack.mixin;

import me.ijese.jorgitohack.JorgitoHack;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class JorgitoHackLoader
        implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public JorgitoHackLoader() {
        JorgitoHack.LOGGER.info("\n\nLoading mixins by iJese");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.jorgitohack.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        JorgitoHack.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

