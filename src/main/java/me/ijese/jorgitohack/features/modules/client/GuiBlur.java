package me.ijese.jorgitohack.features.modules.client;

import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.util.Util;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiModList;

public class GuiBlur
        extends Module
        implements Util {
    public GuiBlur() {
        super("GUIBlur", "Nigga", Module.Category.CLIENT, true, false, false);
    }

    @Override
    public void onDisable() {
        if (GuiBlur.mc.world != null) {
            GuiBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    @Override
    public void onUpdate() {
        if (GuiBlur.mc.world == null) {
            return;
        }
        if (!(ClickGui.getInstance().isEnabled() || GuiBlur.mc.currentScreen instanceof GuiContainer || GuiBlur.mc.currentScreen instanceof GuiChat || GuiBlur.mc.currentScreen instanceof GuiConfirmOpenLink || GuiBlur.mc.currentScreen instanceof GuiEditSign || GuiBlur.mc.currentScreen instanceof GuiGameOver || GuiBlur.mc.currentScreen instanceof GuiOptions || GuiBlur.mc.currentScreen instanceof GuiIngameMenu || GuiBlur.mc.currentScreen instanceof GuiVideoSettings || GuiBlur.mc.currentScreen instanceof GuiScreenOptionsSounds || GuiBlur.mc.currentScreen instanceof GuiControls || GuiBlur.mc.currentScreen instanceof GuiCustomizeSkin || GuiBlur.mc.currentScreen instanceof GuiModList)) {
            if (GuiBlur.mc.entityRenderer.getShaderGroup() == null) {
                return;
            }
            GuiBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            return;
        }
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (GuiBlur.mc.entityRenderer.getShaderGroup() != null) {
                GuiBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            try {
                GuiBlur.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        if (GuiBlur.mc.entityRenderer.getShaderGroup() == null) {
            return;
        }
        if (GuiBlur.mc.currentScreen != null) {
            return;
        }
        GuiBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
    }
}

