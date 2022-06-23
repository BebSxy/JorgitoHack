package me.ijese.jorgitohack.util;

import java.util.Collection;
import java.util.Arrays;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.block.Block;
import java.util.ArrayList;

public class WorldUtils implements Util
{
    public static final ArrayList<Block> empty;
    public static final List<Block> blackList;

    public static Block getBlock(final BlockPos block) {
        return WorldUtils.mc.world.getBlockState(block).getBlock();
    }

    public static List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int)r) : cy; y < (sphere ? (cy + r) : ((float)(cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        circleblocks.add(new BlockPos(x, y + plus_y, z));
                    }
                }
            }
        }
        return circleblocks;
    }

    public static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (WorldUtils.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(WorldUtils.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = WorldUtils.mc.world.getBlockState(neighbour);
                if (!blockState.getBlock().getMaterial(blockState).isReplaceable()) {
                    return side;
                }
            }
        }
        return null;
    }

    public static double getRange(final Vec3d vec) {
        return WorldUtils.mc.player.getPositionVector().add(0.0, (double)WorldUtils.mc.player.eyeHeight, 0.0).distanceTo(vec);
    }

    public static EnumFacing getEnumFacing(final boolean rayTrace, final BlockPos placePosition) {
        final RayTraceResult result = WorldUtils.mc.world.rayTraceBlocks(new Vec3d(WorldUtils.mc.player.posX, WorldUtils.mc.player.posY + WorldUtils.mc.player.getEyeHeight(), WorldUtils.mc.player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() - 0.5, placePosition.getZ() + 0.5));
        if (placePosition.getY() == 255) {
            return EnumFacing.DOWN;
        }
        if (rayTrace) {
            return (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        }
        return EnumFacing.UP;
    }

    public static boolean canBeClicked(final BlockPos pos) {
        return WorldUtils.mc.world.getBlockState(pos).getBlock().canCollideCheck(WorldUtils.mc.world.getBlockState(pos), false);
    }

    public static boolean isWithin(final double distance, final Vec3d vec, final Vec3d vec2) {
        return vec.squareDistanceTo(vec2) <= distance * distance;
    }

    public static boolean isOutside(final double distance, final Vec3d vec, final Vec3d vec2) {
        return vec.squareDistanceTo(vec2) > distance * distance;
    }

    private boolean place(final BlockPos pos) {
        boolean isSneaking = WorldUtils.mc.player.isSneaking();
        final Block block = WorldUtils.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockFire)) {
            return false;
        }
        final EnumFacing side = getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = WorldUtils.mc.world.getBlockState(neighbour).getBlock();
        if (!isSneaking && WorldUtils.blackList.contains(neighbourBlock)) {
            WorldUtils.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)WorldUtils.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }
        WorldUtils.mc.playerController.processRightClickBlock(WorldUtils.mc.player, WorldUtils.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        WorldUtils.mc.player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }

    static {
        empty = new ArrayList<Block>(Arrays.asList(Blocks.AIR, Blocks.VINE, Blocks.SNOW_LAYER, (Block)Blocks.TALLGRASS, (Block)Blocks.FIRE, (Block)Blocks.LAVA, (Block)Blocks.FLOWING_LAVA, (Block)Blocks.FLOWING_WATER, (Block)Blocks.WATER));
        blackList = Arrays.asList(Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    }
}
