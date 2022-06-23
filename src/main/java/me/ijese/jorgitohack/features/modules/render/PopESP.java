package me.ijese.jorgitohack.features.modules.render;

import me.ijese.jorgitohack.event.events.PacketEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.ColorUtil;
import me.ijese.jorgitohack.util.RenderUtil;

import com.mojang.authlib.GameProfile;

import org.lwjgl.opengl.GL11;

import java.awt.Color;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PopESP extends Module {

    public PopESP() {
        super("PopESP", "Highlights when players pop", Category.RENDER, true, false, false);
    }

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public final Setting<Integer> fadeStart = this.register(new Setting("Fade Start", 100, 0, 400));
    public final Setting<Integer> fadeTime = this.register(new Setting("Fade Time", 500, 0, 2000));
    public final Setting<Boolean> self = this.register(new Setting("Render Self", true));
    public final Setting<Integer> fillColorR = this.register(new Setting("Fill Color Red", 15, 0, 255));
    public final Setting<Integer> fillColorG = this.register(new Setting("Fill Color Green", 100, 0, 255));
    public final Setting<Integer> fillColorB = this.register(new Setting("Fill Color Blue", 255, 0, 255));
    public final Setting<Integer> fillColorA = this.register(new Setting("Fill Color Alpha", 100, 0, 255));
    public final Setting<Integer> lineColorR = this.register(new Setting("line Color Red", 15, 0, 255));
    public final Setting<Integer> lineColorG = this.register(new Setting("line Color Green", 100, 0, 255));
    public final Setting<Integer> lineColorB = this.register(new Setting("line Color Blue", 255, 0, 255));
    public final Setting<Integer> lineColorA = this.register(new Setting("line Color Alpha", 255, 0, 255));
    public final Setting<Boolean> glint = this.register(new Setting("Glint", true));
    public final Setting<Integer> glintColorR = this.register(new Setting("glint Color Red", 15, 0, 255));
    public final Setting<Integer> glintColorG = this.register(new Setting("glint Color Green", 100, 0, 255));
    public final Setting<Integer> glintColorB = this.register(new Setting("glint Color Blue", 255, 0, 255));
    public final Setting<Integer> glintColorA = this.register(new Setting("glint Color Alpha", 255, 0, 255));
    EntityOtherPlayerMP player = null;
    ModelPlayer playerModel = null;
    long startTime = 0L;

    public Color getFillColor() {
        return new Color(this.fillColorR.getValue(), this.fillColorG.getValue(), this.fillColorB.getValue(), this.fillColorA.getValue());
    }

    public Color getLineColor() {
        return new Color(this.lineColorR.getValue(), this.lineColorG.getValue(), this.lineColorB.getValue(), this.lineColorA.getValue());
    }

    public Color getGlintColor() {
        return new Color(this.glintColorR.getValue(), this.glintColorG.getValue(), this.glintColorB.getValue(), this.glintColorA.getValue());
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        SPacketEntityStatus packet;
        if (this.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus && (packet = (SPacketEntityStatus)event.getPacket()).getOpCode() == 35 && packet.getEntity((World)PopESP.mc.world) != null && (this.self.getValue().booleanValue() || packet.getEntity((World)PopESP.mc.world).getEntityId() != PopESP.mc.player.getEntityId())) {
            GameProfile profile = new GameProfile(PopESP.mc.player.getUniqueID(), "");
            this.player = new EntityOtherPlayerMP( mc.world, profile);
            this.player.copyLocationAndAnglesFrom(packet.getEntity(PopESP.mc.world));
            this.playerModel = new ModelPlayer(0.0f, false);
            this.startTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (this.nullCheck()) {
            return;
        }
        GL11.glLineWidth((float)1.0f);
        float[] glintHSB = Color.RGBtoHSB(this.getGlintColor().getRed(), this.getGlintColor().getGreen(), this.getGlintColor().getBlue(), null);
        int lineA = this.getLineColor().getAlpha();
        int fillA = this.getFillColor().getAlpha();
        int glintA = this.getGlintColor().getAlpha();
        float glintB = glintHSB[2];
        if (System.currentTimeMillis() - this.startTime > this.fadeStart.getValue().longValue()) {
            long time = System.currentTimeMillis() - this.startTime - this.fadeStart.getValue().longValue();
            double normal = this.normalize(time, 0.0, this.fadeTime.getValue().doubleValue());
            normal = MathHelper.clamp((double)normal, (double)0.0, (double)1.0);
            normal = -normal + 1.0;
            lineA = (int)(normal * (double)lineA);
            fillA = (int)(normal * (double)fillA);
            glintA = (int)(normal * (double)glintA);
            glintB = (float)(normal * (double)glintB);
        }
        Color lineColor = ColorUtil.newAlpha(getLineColor(), lineA);
        Color fillColor = ColorUtil.newAlpha(getFillColor(), fillA);
        Color finalGlintColor = ColorUtil.newAlpha(Color.getHSBColor(glintHSB[0], glintHSB[1], glintB), glintA);
        if (this.player != null && this.playerModel != null) {
            RenderUtil.prepare();
            ColorUtil.glColor(fillColor);
            GL11.glPolygonMode((int)1032, (int)6914);
            RenderUtil.renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0f);
            if (this.glint.getValue().booleanValue()) {
                PopESP.mc.getRenderManager().renderEngine.bindTexture(RES_ITEM_GLINT);
                GL11.glTexCoord3d((double)1.0, (double)1.0, (double)1.0);
                GL11.glEnable((int)3553);
                GL11.glBlendFunc((int)768, (int)771);
                ColorUtil.glColor(finalGlintColor);
                RenderUtil.renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0f);
                GL11.glBlendFunc((int)770, (int)771);
            }
            ColorUtil.glColor(lineColor);
            GL11.glPolygonMode((int)1032, (int)6913);
            RenderUtil.renderEntity((EntityLivingBase)this.player, (ModelBase)this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0f);
            GL11.glPolygonMode((int)1032, (int)6914);
            RenderUtil.release();
        }
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

}
