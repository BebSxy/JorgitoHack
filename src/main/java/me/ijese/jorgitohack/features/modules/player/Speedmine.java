package me.ijese.jorgitohack.features.modules.player;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.event.events.BlockEvent;
import me.ijese.jorgitohack.event.events.PacketEvent;
import me.ijese.jorgitohack.event.events.Render3DEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Bind;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Speedmine extends Module
{
    public final Setting<SwitchMode> autoSwitch;
    public final Setting<InstantMode> instantMode;
    public final Setting<Bind> instantBind;
    public final Setting<Integer> instantDelay;
    public final Setting<Boolean> swing;
    public final Setting<Boolean> render;
    public final Setting<Boolean> statusColor;
    public final Setting<RenderMode> renderMode;
    public final Setting<ColorMode> statusMode;
    public final Setting<Integer> red;
    public final Setting<Integer> green;
    public final Setting<Integer> blue;
    public final Setting<Integer> alpha;
    public Timer timer;
    public Timer instantTimer;
    public Block block;
    public BlockPos breakPos;
    public EnumFacing enumFacing;
    public static float damage;

    public Speedmine() {
        super("Speedmine", "Allows you to mine blocks faster than normal", Category.PLAYER, true, false, false);
        this.autoSwitch = (Setting<SwitchMode>)this.register(new Setting("Auto Switch", SwitchMode.SILENT));
        this.instantMode = (Setting<InstantMode>)this.register(new Setting("Instant Mode", InstantMode.NONE));
        this.instantBind = (Setting<Bind>)this.register(new Setting("Instant Bind", new Bind(-1)));
        this.instantDelay = (Setting<Integer>)this.register(new Setting("Instant Delay", 1, 0, 50));
        this.swing = (Setting<Boolean>)this.register(new Setting("Swing", true));
        this.render = (Setting<Boolean>)this.register(new Setting("Render", true));
        this.statusColor = (Setting<Boolean>)this.register(new Setting("Status Color", false));
        this.renderMode = (Setting<RenderMode>)this.register(new Setting("Render Mode", RenderMode.RISE));
        this.statusMode = (Setting<ColorMode>)this.register(new Setting("Status Mode", ColorMode.CUSTOM));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 30, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 167, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 255, 0, 255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 150, 0, 255));
        this.timer = new Timer();
        this.instantTimer = new Timer();
        this.block = null;
        this.breakPos = null;
        this.enumFacing = null;
    }

    @SubscribeEvent
    public void onBlock(final BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (isBlockValid(event.pos)) {
            this.breakPos = event.pos;
            this.enumFacing = event.facing;
            if (this.swing.getValue()) {
                Speedmine.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.timer.reset();
            this.instantTimer.reset();
            Speedmine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.breakPos, this.enumFacing));
            Speedmine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.breakPos, this.enumFacing));
        }
    }

    @Override
    public void onDisable() {
        this.breakPos = null;
        this.enumFacing = null;
    }

    @Override
    public void onLogout() {
        this.breakPos = null;
        this.enumFacing = null;
    }

    @Override
    public void onUpdate() {
        if (this.breakPos != null) {
            final int oldSlot = Speedmine.mc.player.inventory.currentItem;
            try {
                this.block = Speedmine.mc.world.getBlockState(this.breakPos).getBlock();
            }
            catch (Exception ex) {}
            final int toolSlot = this.getBestAvailableToolSlot(this.block.getBlockState().getBaseState());
            final float breakTime = Speedmine.mc.world.getBlockState(this.breakPos).getBlockHardness((World)Speedmine.mc.world, this.breakPos);
            if (Speedmine.mc.world.getBlockState(this.breakPos).getBlock() == Blocks.AIR && this.instantMode.getValue() != InstantMode.NORMAL) {
                if (this.instantMode.getValue() == InstantMode.BIND && !this.instantBind.getValue().isDown()) {
                    this.breakPos = null;
                    this.enumFacing = null;
                    return;
                }
                if (this.instantMode.getValue() == InstantMode.NONE) {
                    this.breakPos = null;
                    this.enumFacing = null;
                    return;
                }
            }
            else if (this.instantMode.getValue() == InstantMode.NORMAL) {}
            if (this.timer.passed((long)breakTime)) {
                if (this.autoSwitch.getValue() == SwitchMode.SILENT && Speedmine.mc.player.inventory.currentItem != toolSlot && toolSlot != -1) {
                    Speedmine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(toolSlot));
                    Speedmine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.breakPos, this.enumFacing));
                    Speedmine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(oldSlot));
                }
            }
        }
    }

    @SubscribeEvent
    @Override
    public void onRender3D(final Render3DEvent event) {
        if (Speedmine.mc.player != null && Speedmine.mc.world != null && this.render.getValue() && this.breakPos != null && Speedmine.mc.world.getBlockState(this.breakPos).getBlock() != Blocks.AIR) {
            final Color renderColors = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
            final AxisAlignedBB bb = new AxisAlignedBB(this.breakPos.getX() - Speedmine.mc.getRenderManager().viewerPosX, this.breakPos.getY() - Speedmine.mc.getRenderManager().viewerPosY, this.breakPos.getZ() - Speedmine.mc.getRenderManager().viewerPosZ, this.breakPos.getX() + 1 - Speedmine.mc.getRenderManager().viewerPosX, this.breakPos.getY() + 1 - Speedmine.mc.getRenderManager().viewerPosY, this.breakPos.getZ() + 1 - Speedmine.mc.getRenderManager().viewerPosZ);
            final float breakTime = Speedmine.mc.world.getBlockState(this.breakPos).getBlockHardness((World)Speedmine.mc.world, this.breakPos);
            final double progression = this.timer.getPassedTimeMs() / 40.0f / breakTime * JorgitoHack.serverManager.getTpsFactor();
            final double centerX = bb.minX + (bb.maxX - bb.minX) / Double.longBitsToDouble(Double.doubleToLongBits(0.4677600299948414) ^ 0x7FDDEFC7C3CD0B6FL);
            final double increaseX = progression * ((bb.maxX - centerX) / Double.longBitsToDouble(Double.doubleToLongBits(0.13526251945472825) ^ 0x7FE5504840B76025L));
            final double centerY = bb.minY + (bb.maxY - bb.minY) / Double.longBitsToDouble(Double.doubleToLongBits(0.2265017733140163) ^ 0x7FCCFE02966F5283L);
            final double increaseY = progression * ((bb.maxY - centerY) / Double.longBitsToDouble(Double.doubleToLongBits(0.1390228780814261) ^ 0x7FE5CB806D60B4E4L));
            final double centerZ = bb.minZ + (bb.maxZ - bb.minZ) / Double.longBitsToDouble(Double.doubleToLongBits(0.7187776739735182) ^ 0x7FE7003A0959F571L);
            final double increaseZ = progression * ((bb.maxZ - centerZ) / Double.longBitsToDouble(Double.doubleToLongBits(0.2497058873358051) ^ 0x7FEBF65CCDDCEBB7L));
            final float shrinkFactor = MathHelper.clamp((float)increaseX, Float.intBitsToFloat(Float.floatToIntBits(-14.816234f) ^ 0x7EED0F4B), Float.intBitsToFloat(Float.floatToIntBits(9.43287f) ^ 0x7E96ED09));
            final AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - increaseX, centerY - increaseY, centerZ - increaseZ, centerX + increaseX, centerY + increaseY, centerZ + increaseZ);
            final double oldMaxY = bb.maxY;
            final double upY = progression * (bb.maxY - bb.minY);
            final AxisAlignedBB riseBB = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + upY, bb.maxZ);
            final float sGreen = MathHelper.clamp((float)upY, 0.0f, 1.0f);
            final Color color = this.statusMode.getValue().equals(ColorMode.STATIC) ? new Color(this.timer.hasReached((long)breakTime) ? 0 : 255, this.timer.hasReached((long)breakTime) ? 255 : 0, 0, this.alpha.getValue()) : new Color(255 - (int)(sGreen * 150.0f), (int)(sGreen * 255.0f), 0, this.alpha.getValue());
            final float fadeA = MathHelper.clamp((float)upY, 0.0f, 1.0f);
            final Color alphaFade = new Color(renderColors.getRed(), renderColors.getGreen(), renderColors.getBlue(), (int)(fadeA * 255.0f));
            if (this.renderMode.getValue().equals(RenderMode.RISE)) {
                if (riseBB.maxY <= oldMaxY) {
                    RenderUtil.drawFilledBox(riseBB, ((boolean)this.statusColor.getValue()) ? color.getRGB() : renderColors.getRGB());
                    RenderUtil.drawBlockOutline(riseBB, ((boolean)this.statusColor.getValue()) ? color : renderColors, 1.0f);
                }
                if (riseBB.maxY >= oldMaxY) {
                    RenderUtil.drawFilledBox(bb, ((boolean)this.statusColor.getValue()) ? color.getRGB() : renderColors.getRGB());
                    RenderUtil.drawBlockOutline(bb, ((boolean)this.statusColor.getValue()) ? color : renderColors, 1.0f);
                }
            }
            else if (this.renderMode.getValue().equals(RenderMode.GROW)) {
                if (axisAlignedBB1.maxY <= oldMaxY) {
                    RenderUtil.drawFilledBox(axisAlignedBB1, ((boolean)this.statusColor.getValue()) ? color.getRGB() : renderColors.getRGB());
                    RenderUtil.drawBlockOutline(axisAlignedBB1, ((boolean)this.statusColor.getValue()) ? color : renderColors, Float.intBitsToFloat(Float.floatToIntBits(7.8420115f) ^ 0x7F7AF1C2));
                }
                if (axisAlignedBB1.maxY >= oldMaxY) {
                    RenderUtil.drawFilledBox(bb, ((boolean)this.statusColor.getValue()) ? color.getRGB() : renderColors.getRGB());
                    RenderUtil.drawBlockOutline(bb, ((boolean)this.statusColor.getValue()) ? color : renderColors, Float.intBitsToFloat(Float.floatToIntBits(117.64367f) ^ 0x7D6B498F));
                }
            }
            else if (this.renderMode.getValue().equals(RenderMode.STATIC)) {
                RenderUtil.drawFilledBox(bb, ((boolean)this.statusColor.getValue()) ? color.getRGB() : renderColors.getRGB());
                RenderUtil.drawBlockOutline(bb, ((boolean)this.statusColor.getValue()) ? color : renderColors, 1.0f);
            }
            else if (this.renderMode.getValue().equals(RenderMode.FADE)) {
                RenderUtil.drawFilledBox(bb, ((boolean)this.statusColor.getValue()) ? new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(fadeA * 255.0f)).getRGB() : alphaFade.getRGB());
                RenderUtil.drawBlockOutline(bb, ((boolean)this.statusColor.getValue()) ? new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(fadeA * 255.0f)) : alphaFade, 1.0f);
            }
            else if (this.renderMode.getValue().equals(RenderMode.ANIMATE)) {
                RenderUtil.drawFilledBox(RenderUtil.fixBB(new AxisAlignedBB(this.breakPos)).shrink((double)shrinkFactor), ((boolean)this.statusColor.getValue()) ? color.getRGB() : renderColors.getRGB());
                RenderUtil.drawBlockOutline(RenderUtil.fixBB(new AxisAlignedBB(this.breakPos)).shrink((double)shrinkFactor), ((boolean)this.statusColor.getValue()) ? color : renderColors, Float.intBitsToFloat(Float.floatToIntBits(119.3883f) ^ 0x7D6EC6CF));
            }
        }
    }

    public int getBestAvailableToolSlot(final IBlockState blockState) {
        int toolSlot = -1;
        double max = 0.0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Speedmine.mc.player.inventory.getStackInSlot(i);
            float speed;
            if (!stack.isEmpty && (speed = stack.getDestroySpeed(blockState)) > 1.0f) {
                final int eff;
                if ((speed += (float)(((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0) + 1.0) : 0.0)) > max) {
                    max = speed;
                    toolSlot = i;
                }
            }
        }
        return toolSlot;
    }

    public static boolean isBlockValid(final BlockPos pos) {
        final IBlockState blockState = Speedmine.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)Speedmine.mc.world, pos) != -1.0f;
    }

    public enum ColorMode
    {
        STATIC,
        CUSTOM;
    }

    public enum SwitchMode
    {
        SILENT,
        AUTO,
        NONE;
    }

    public enum RenderMode
    {
        RISE,
        FADE,
        STATIC,
        ANIMATE,
        GROW;
    }

    public enum InstantMode
    {
        NONE,
        NORMAL,
        BIND;
    }
}
