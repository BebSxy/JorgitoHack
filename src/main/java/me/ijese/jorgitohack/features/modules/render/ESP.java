package me.ijese.jorgitohack.features.modules.render;

import me.ijese.jorgitohack.event.events.Render3DEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.modules.client.Colors;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.EntityUtil;
import me.ijese.jorgitohack.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ESP extends Module {
    private static ESP INSTANCE;
    public final Setting<Boolean> colorSync;
    public final Setting<Boolean> players;
    public final Setting<Boolean> colorFriends;
    public final Setting<Boolean> animals;
    public final Setting<Boolean> mobs;
    public final Setting<Boolean> others;
    public final Setting<Boolean> items;
    public final Setting<Boolean> xporbs;
    public final Setting<Boolean> xpbottles;
    public final Setting<Boolean> pearl;
    public final Setting<Integer> red;
    public final Setting<Integer> green;
    public final Setting<Integer> blue;
    public final Setting<Integer> boxAlpha;
    public final Setting<Integer> alpha;
    public final Setting<Float> lineWidth;


    public ESP() {
        super("ESP", "Draws boxes around certain entities or events.", Module.Category.RENDER, true, false, false);
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.players = (Setting<Boolean>)this.register(new Setting("Players", true));
        this.colorFriends = (Setting<Boolean>)this.register(new Setting("Friends", true, v -> this.players.getValue()));
        this.animals = (Setting<Boolean>)this.register(new Setting("Animals", false));
        this.mobs = (Setting<Boolean>)this.register(new Setting("Mobs", false));
        this.others = (Setting<Boolean>)this.register(new Setting("Crystals", false));
        this.items = (Setting<Boolean>)this.register(new Setting("Items", false));
        this.xporbs = (Setting<Boolean>)this.register(new Setting("XpOrbs", false));
        this.xpbottles = (Setting<Boolean>)this.register(new Setting("XpBottles", false));
        this.pearl = (Setting<Boolean>)this.register(new Setting("Pearls", false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 255, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 255, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", 120, 0, 255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", 2.0f, 0.1f, 5.0f));
        this.setInstance();
    }

    public static ESP getInstance() {
        if (ESP.INSTANCE == null) {
            ESP.INSTANCE = new ESP();
        }
        return ESP.INSTANCE;
    }

    private void setInstance() {
        ESP.INSTANCE = this;
    }

    @Override
    public void onRender3D(final Render3DEvent event) {
        if (this.items.getValue()) {
            int i = 0;
            for (final Entity entity : ESP.mc.world.loadedEntityList) {
                if (entity instanceof EntityItem) {
                    if (ESP.mc.player.getDistanceSq(entity) >= 2500.0) {
                        continue;
                    }
                    final Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, ESP.mc.getRenderPartialTicks());
                    final AxisAlignedBB bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0f);
                    RenderGlobal.renderFilledBox(bb, ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0f) : (this.red.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f) : (this.green.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f) : (this.blue.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? ((float)Colors.INSTANCE.getCurrentColor().getAlpha()) : (this.boxAlpha.getValue() / 255.0f));
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, ((boolean)this.colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                    if (++i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }
        if (this.xporbs.getValue()) {
            int i = 0;
            for (final Entity entity : ESP.mc.world.loadedEntityList) {
                if (entity instanceof EntityXPOrb) {
                    if (ESP.mc.player.getDistanceSq(entity) >= 2500.0) {
                        continue;
                    }
                    final Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, ESP.mc.getRenderPartialTicks());
                    final AxisAlignedBB bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0f);
                    RenderGlobal.renderFilledBox(bb, ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0f) : (this.red.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f) : (this.green.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f) : (this.blue.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0f) : (this.boxAlpha.getValue() / 255.0f));
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, ((boolean)this.colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                    if (++i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }
        if (this.pearl.getValue()) {
            int i = 0;
            for (final Entity entity : ESP.mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderPearl) {
                    if (ESP.mc.player.getDistanceSq(entity) >= 2500.0) {
                        continue;
                    }
                    final Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, ESP.mc.getRenderPartialTicks());
                    final AxisAlignedBB bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0f);
                    RenderGlobal.renderFilledBox(bb, ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0f) : (this.red.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f) : (this.green.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f) : (this.blue.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0f) : (this.boxAlpha.getValue() / 255.0f));
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, ((boolean)this.colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                    if (++i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }
        if (this.xpbottles.getValue()) {
            int i = 0;
            for (final Entity entity : ESP.mc.world.loadedEntityList) {
                if (entity instanceof EntityExpBottle) {
                    if (ESP.mc.player.getDistanceSq(entity) >= 2500.0) {
                        continue;
                    }
                    final Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, ESP.mc.getRenderPartialTicks());
                    final AxisAlignedBB bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0f);
                    RenderGlobal.renderFilledBox(bb, ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0f) : (this.red.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0f) : (this.green.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0f) : (this.blue.getValue() / 255.0f), ((boolean)this.colorSync.getValue()) ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0f) : (this.boxAlpha.getValue() / 255.0f));
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, ((boolean)this.colorSync.getValue()) ? Colors.INSTANCE.getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                    if (++i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }
    }

    static {
        ESP.INSTANCE = new ESP();
    }


}
