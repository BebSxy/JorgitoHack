package me.ijese.jorgitohack.features.modules.combat;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.modules.Module;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.HoleUtil;
import me.ijese.jorgitohack.util.InventoryUtil;
import me.ijese.jorgitohack.util.RotationUtil;
import me.ijese.jorgitohack.util.ColorUtil;
import me.ijese.jorgitohack.util.RenderUtil;
import me.ijese.jorgitohack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static me.ijese.jorgitohack.util.RotationUtil.getEyesPos;

public class Holefill extends Module {

    public Holefill() {
        super("Holefill", "Automatically fills holes around your opponent", Category.COMBAT, true, false,false);
    }
//from Kami five
    public enum SwitchMode {
        Normal,
        Silent,
        Require
    }

    public final Setting<SwitchMode> switchMode = this.register(new Setting("Switch Mode", SwitchMode.Silent));
    public final Setting<Double> range = this.register(new Setting("Range", 5.0D, 1.0D, 10.0D));
    public final Setting<Double> wallRange = this.register(new Setting("Walls Range", 3.0D, 1.0D, 10.0D));
    public final Setting<Integer> delay = this.register(new Setting("Delay", 1, 0, 1000));
    public final Setting<Integer> blocksPerTick = this.register(new Setting("Blocks Per Tick", 4, 1, 10));
    public final Setting<Boolean> disableAfter = this.register(new Setting("Disable When Done", false));
    public final Setting<Boolean> rotate = this.register(new Setting("Rotate", false));
    public final Setting<Boolean> doubles = this.register(new Setting("Doubles", true));
    public final Setting<Boolean> smart = this.register(new Setting("Smart", true));
    public final Setting<Double> smartTargetRange = this.register(new Setting("Target Range", 10.0D, 1.0D, 10.0D));
    public final Setting<Double> smartBlockRange = this.register(new Setting("Smart Hole Range", 3.0D, 1.0D, 10.0D));
    public final Setting<Boolean> noSelfFill = this.register(new Setting("Anti Self Fill", true));
    public final Setting<Double> selfDist = this.register(new Setting("No Self Distance", 2.0D, 1.0D, 3.0D));
    public final Setting<Boolean> renderHoles = this.register(new Setting("Render Holes", true));
    public final Setting<Integer> Lred = this.register(new Setting("Line Red", 255, 0, 255));
    public final Setting<Integer> Lgreen = this.register(new Setting("Line Green", 255, 0, 255));
    public final Setting<Integer> Lblue = this.register(new Setting("Line Blue", 255, 0, 255));
    public final Setting<Integer> Lalpha = this.register(new Setting("Line Alpha", 255, 0, 255));
    public final Setting<Integer> Fred = this.register(new Setting("Fill Red", 255, 0, 255));
    public final Setting<Integer> Fgreen = this.register(new Setting("Fill Green", 255, 0, 255));
    public final Setting<Integer> Fblue = this.register(new Setting("Fill Blue", 255, 0, 255));
    public final Setting<Integer> Falpha = this.register(new Setting("Fill Alpha", 100, 0, 255));
    public final Setting<Double> fadeTime = this.register(new Setting("Fade Time", 200, 0, 1000));
    static List<BlockPos> tickCache = new ArrayList<BlockPos>();
    Timer timeSystem = new Timer();
    List<HoleUtil.Hole> holes = new ArrayList<HoleUtil.Hole>();
    Map<HoleUtil.Hole, Long> renderPositions = new HashMap<HoleUtil.Hole, Long>();
    Entity target;
    long currentTime;

    public Color getFillColor() {
        return new Color(this.Fred.getValue(), this.Fgreen.getValue(), this.Fblue.getValue(), this.Falpha.getValue());
    }

    public Color getLineColor() {
        return new Color(this.Lred.getValue(), this.Lgreen.getValue(), this.Lblue.getValue(), this.Lalpha.getValue());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timeSystem.reset();
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if (this.nullCheck()) {
            return;
        }
        this.currentTime = System.currentTimeMillis();
        this.target = getTarget(this.smartTargetRange.getValue().doubleValue());
        this.timeSystem.setDelay(this.delay.getValue().longValue());
        int blocksPlaced = 0;
        if (this.timeSystem.isPassed()) {
            this.getHoles();
            if (this.holes == null || this.holes.size() == 0) {
                if (this.disableAfter.getValue().booleanValue()) {
                    this.setEnabled(false);
                }
                return;
            }
            if (this.switchMode.getValue()== SwitchMode.Require && Holefill.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Item.getItemFromBlock((Block)Blocks.OBSIDIAN)) {
                return;
            }
            int oldSlot = Holefill.mc.player.inventory.currentItem;
            int blockSlot = InventoryUtil.getHotbarItemSlot(Item.getItemFromBlock((Block)Blocks.OBSIDIAN));
            if (blockSlot == -1) {
                return;
            }
            boolean switched = false;
            for (HoleUtil.Hole hole : this.holes) {
                if (!switched) {
                    this.doSwitch(blockSlot);
                    switched = true;
                }
                this.doRotate(hole.pos1);
                if (hole.doubleHole) {
                    placeBlock(hole.pos1, true);
                    placeBlock(hole.pos2, true);
                } else {
                    placeBlock(hole.pos1, true);
                }
                if (this.renderPositions.containsKey(hole) && this.currentTime - this.renderPositions.get(hole) > this.fadeTime.getValue().longValue()) {
                    this.renderPositions.put(hole, System.currentTimeMillis());
                }
                if (++blocksPlaced < this.blocksPerTick.getValue().intValue()) continue;
                break;
            }
            if (this.switchMode.getValue() == SwitchMode.Silent && switched) {
                this.doSwitch(oldSlot);
            }
            this.timeSystem.reset();
        } else {
            if (RotationUtil.INSTANCE.rotating) {
                RotationUtil.INSTANCE.resetRotations();
            }
            RotationUtil.INSTANCE.rotating = false;
        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (this.nullCheck()) {
            return;
        }
        if (!this.renderHoles.getValue().booleanValue()) {
            return;
        }
        for (Map.Entry<HoleUtil.Hole, Long> entry : this.renderPositions.entrySet()) {
            int fillAlpha = getFillColor().getAlpha();
            int lineAlpha = getLineColor().getAlpha();
            long time = System.currentTimeMillis() - entry.getValue();
            double normal = normalize(time, 0.0, this.fadeTime.getValue().doubleValue());
            normal = MathHelper.clamp((double)normal, (double)0.0, (double)1.0);
            normal = -normal;
            fillAlpha = (int)((double)fillAlpha * (normal += 1.0));
            lineAlpha = (int)((double)lineAlpha * normal);
            Color fillColor = ColorUtil.newAlpha(getFillColor(), fillAlpha);
            Color lineColor = ColorUtil.newAlpha(getLineColor(), lineAlpha);
            HoleUtil.Hole hole = entry.getKey();
            AxisAlignedBB bb = hole.doubleHole ? new AxisAlignedBB((double)hole.pos1.getX(), (double)hole.pos1.getY(), (double)hole.pos1.getZ(), (double)(hole.pos2.getX() + 1), (double)(hole.pos2.getY() + 1), (double)(hole.pos2.getZ() + 1)) : new AxisAlignedBB(hole.pos1);
            RenderUtil.renderBB(7, bb, fillColor, fillColor);
            RenderUtil.renderBB(3, bb, lineColor, lineColor);
        }
    }

    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public void getHoles() {
        this.loadHoles();
    }

    public static boolean placeBlock(BlockPos pos, boolean sneak) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        EnumFacing side = getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking()) {
            mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        EnumActionResult action = mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        tickCache.add(pos);
        return action == EnumActionResult.SUCCESS;
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) && tickCache.contains(neighbour) || (blockState = mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            return side;
        }
        return null;
    }

    public void loadHoles() {
        this.holes = HoleUtil.getHoles(this.range.getValue().doubleValue(), Holefill.mc.player.getPosition(), this.doubles.getValue()).stream().filter(hole -> {
            boolean isAllowedHole = true;
            AxisAlignedBB bb = hole.doubleHole ? new AxisAlignedBB((double)hole.pos1.getX(), (double)hole.pos1.getY(), (double)hole.pos1.getZ(), (double)(hole.pos2.getX() + 1), (double)(hole.pos2.getY() + 1), (double)(hole.pos2.getZ() + 1)) : new AxisAlignedBB(hole.pos1);
            for (Entity e : Holefill.mc.world.getEntitiesWithinAABB(Entity.class, bb)) {
                isAllowedHole = false;
            }
            return isAllowedHole;
        }).filter(hole -> {
            boolean isAllowedSmart = false;
            if (this.smart.getValue().booleanValue()) {
                if (this.target != null && this.target.getDistance((double)hole.pos1.getX() + 0.5, (double)(hole.pos1.getY() + 1), (double)hole.pos1.getZ() + 0.5) < this.smartBlockRange.getValue().doubleValue()) {
                    isAllowedSmart = true;
                }
            } else {
                isAllowedSmart = true;
            }
            return isAllowedSmart;
        }).filter(hole -> {
            BlockPos pos = hole.pos1.add(0, 1, 0);
            boolean raytrace = Holefill.mc.world.rayTraceBlocks(getEyesPos(), new Vec3d((Vec3i)pos)) != null;
            return !raytrace || Holefill.mc.player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) <= this.wallRange.getValue().doubleValue();
        }).collect(Collectors.toList());
    }

    public void doSwitch(int i) {
        if (this.switchMode.getValue() == SwitchMode.Normal) {
            InventoryUtil.switchToSlot(i);
        }
        if (this.switchMode.getValue() == SwitchMode.Silent) {
            InventoryUtil.switchToSlotGhost(i);
        }
    }

    public void doRotate(BlockPos pos) {
        if (this.rotate.getValue().booleanValue()) {
            if (!RotationUtil.INSTANCE.rotating) {
                RotationUtil.INSTANCE.rotating = true;
            }
            RotationUtil.INSTANCE.rotate(new Vec3d((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static EntityLivingBase getTarget(double targetRange) {
        return (EntityLivingBase) mc.world.getLoadedEntityList().stream().filter(Objects::nonNull).filter(entity -> entity instanceof EntityPlayer).filter(Holefill::isAlive).filter(entity -> entity.getEntityId() != mc.player.getEntityId()).filter(entity -> !JorgitoHack.friendManager.isFriend((EntityPlayer)entity)).filter(entity -> (double)mc.player.getDistance(entity) <= targetRange).min(Comparator.comparingDouble(entity -> mc.player.getDistance(entity))).orElse(null);
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0f;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

}
