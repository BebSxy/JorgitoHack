package me.ijese.jorgitohack.features.modules.render;

import me.ijese.jorgitohack.event.events.Render3DEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.modules.client.ClickGui;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class BreadCrumbs extends Module {

    public BreadCrumbs() {
        super("BreadCrumbs", "Draws a small line behind you", Category.RENDER, true, false, false);
        this.length = (Setting<Integer>)this.register(new Setting("Length", 15, 5, 40));
        this.width = (Setting<Float>)this.register(new Setting("Width", (Float)Float.intBitsToFloat(Float.floatToIntBits(15.599429f) ^ 0x7EB99743), (Float)Float.intBitsToFloat(Float.floatToIntBits(2.076195f) ^ 0x7F04E061), (Float)Float.intBitsToFloat(Float.floatToIntBits(1.3190416f) ^ 0x7F08D65B)));
        this.syncColor = (Setting<Boolean>)this.register(new Setting("Sync", false));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 30, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 167, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 255, 0, 255));
        this.vecs = new ArrayList<double[]>();
    }

    public static Setting<Integer> length;
    public static Setting<Float> width;
    public static Setting<Boolean> syncColor;
    public static Setting<Integer> red;
    public static Setting<Integer> green;
    public static Setting<Integer> blue;
    public static Setting<Integer> alpha;
    public static ArrayList<double[]> vecs;
    public Color color;

    public Color getCurrentColor() {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    public void onUpdate() {
        if (BreadCrumbs.syncColor.getValue()) {
            this.color = ClickGui.getInstance().getCurrentColor();
        }
        else {
            this.color = getCurrentColor();
        }
        try {
            final double renderPosX = BreadCrumbs.mc.getRenderManager().renderPosX;
            final double renderPosY = BreadCrumbs.mc.getRenderManager().renderPosY;
            final double renderPosZ = BreadCrumbs.mc.getRenderManager().renderPosZ;
            if (this.isEnabled()) {
                final Iterator<EntityPlayer> iterator = BreadCrumbs.mc.world.playerEntities.iterator();
                while (iterator.hasNext()) {
                    final EntityPlayer next;
                    if ((next = iterator.next()) instanceof EntityPlayer) {
                        final EntityPlayer entityPlayer;
                        final boolean b = (entityPlayer = next) == BreadCrumbs.mc.player;
                        double n = renderPosY + Double.longBitsToDouble(Double.doubleToLongBits(0.48965838138858014) ^ 0x7FDF56901B91AE07L);
                        if (BreadCrumbs.mc.player.isElytraFlying()) {
                            n -= Double.longBitsToDouble(Double.doubleToLongBits(29.56900080933637) ^ 0x7FC591AA097B7F4BL);
                        }
                        if (!b) {
                            continue;
                        }
                        BreadCrumbs.vecs.add(new double[] { renderPosX, n - entityPlayer.height, renderPosZ });
                    }
                }
            }
        }
        catch (Exception ex) {}
        if (BreadCrumbs.vecs.size() > BreadCrumbs.length.getValue().intValue()) {
            BreadCrumbs.vecs.remove(0);
        }
    }

    public void onDisable() {
        BreadCrumbs.vecs.removeAll(BreadCrumbs.vecs);
    }

    public static double M(final double n) {
        if (n == Double.longBitsToDouble(Double.doubleToLongBits(1.7931000183463725E308) ^ 0x7FEFEB11C3AAD037L)) {
            return n;
        }
        if (n < Double.longBitsToDouble(Double.doubleToLongBits(1.1859585260803721E308) ^ 0x7FE51C5AEE8AD07FL)) {
            return n * Double.longBitsToDouble(Double.doubleToLongBits(-12.527781766526259) ^ 0x7FD90E3969654F8FL);
        }
        return n;
    }

    public void onRender3D(final Render3DEvent event) {
        try {
            final double renderPosX = BreadCrumbs.mc.getRenderManager().renderPosX;
            final double renderPosY = BreadCrumbs.mc.getRenderManager().renderPosY;
            final double renderPosZ = BreadCrumbs.mc.getRenderManager().renderPosZ;
            final float n = this.color.getRed() / Float.intBitsToFloat(Float.floatToIntBits(0.49987957f) ^ 0x7D80F037);
            final float n2 = this.color.getGreen() / Float.intBitsToFloat(Float.floatToIntBits(0.4340212f) ^ 0x7DA13807);
            final float n3 = this.color.getBlue() / Float.intBitsToFloat(Float.floatToIntBits(0.0131841665f) ^ 0x7F270267);
            if (this.isEnabled()) {
                prepareGL();
                GL11.glPushMatrix();
                GL11.glEnable(2848);
                GL11.glLineWidth(BreadCrumbs.width.getValue().floatValue());
                GL11.glBlendFunc(770, 771);
                GL11.glLineWidth(BreadCrumbs.width.getValue().floatValue());
                GL11.glDepthMask(false);
                GL11.glBegin(3);
                Iterator<double[]> iterator3;
                final Iterator<double[]> iterator2 = iterator3 = BreadCrumbs.vecs.iterator();
                while (iterator3.hasNext()) {
                    final double[] array;
                    final double m;
                    if ((m = M(Math.hypot((array = iterator2.next())[0] - BreadCrumbs.mc.player.posX, array[1] - BreadCrumbs.mc.player.posY))) > BreadCrumbs.length.getValue().intValue()) {
                        iterator3 = iterator2;
                    }
                    else {
                        GL11.glColor4f(n, n2, n3, Float.intBitsToFloat(Float.floatToIntBits(14.099797f) ^ 0x7EE198C5) - (float)(m / BreadCrumbs.length.getValue().intValue()));
                        iterator3 = iterator2;
                        GL11.glVertex3d(array[0] - renderPosX, array[1] - renderPosY, array[2] - renderPosZ);
                    }
                }
                GL11.glEnd();
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
                releaseGL();
            }
        }
        catch (Exception ex) {}
    }

    public static void prepareGL() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(Float.intBitsToFloat(Float.floatToIntBits(5.0675106f) ^ 0x7F22290C));
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(Float.intBitsToFloat(Float.floatToIntBits(11.925059f) ^ 0x7EBECD0B), Float.intBitsToFloat(Float.floatToIntBits(18.2283f) ^ 0x7E11D38F), Float.intBitsToFloat(Float.floatToIntBits(9.73656f) ^ 0x7E9BC8F3));
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.color(Float.intBitsToFloat(Float.floatToIntBits(12.552789f) ^ 0x7EC8D839), Float.intBitsToFloat(Float.floatToIntBits(7.122752f) ^ 0x7F63ED96), Float.intBitsToFloat(Float.floatToIntBits(5.4278784f) ^ 0x7F2DB12E));
        GL11.glColor4f(Float.intBitsToFloat(Float.floatToIntBits(10.5715685f) ^ 0x7EA92525), Float.intBitsToFloat(Float.floatToIntBits(4.9474883f) ^ 0x7F1E51D3), Float.intBitsToFloat(Float.floatToIntBits(4.9044757f) ^ 0x7F1CF177), Float.intBitsToFloat(Float.floatToIntBits(9.482457f) ^ 0x7E97B825));
    }

}
