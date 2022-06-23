package me.ijese.jorgitohack.features.modules.player;

import me.ijese.jorgitohack.event.events.PacketEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import com.mojang.authlib.GameProfile;
import me.ijese.jorgitohack.util.DamageUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FakePlayer
        extends Module {
    public Setting<Boolean> copyInv = this.register(new Setting<Boolean>("Copy Inventory",true));
    public Setting<Boolean> pops = this.register(new Setting<Boolean>("Pops", false));
    public EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns in a fake player", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (FakePlayer.mc.world == null) {
            return;
        }
        this.spawnSinglePlayer();
    }

    @Override
    public void onDisable() {
        if (FakePlayer.mc.world == null) {
            return;
        }
        FakePlayer.mc.world.removeEntityFromWorld(-100);
    }

    @Override
    public void onUpdate() {
        block2: {
            if (!pops.getValue()) break block2;
            if (this.fakePlayer != null) {
                this.fakePlayer.inventory.offHandInventory.set(0, (ItemStack) new ItemStack(Items.TOTEM_OF_UNDYING));
                if (this.fakePlayer.getHealth() <= Float.intBitsToFloat(Float.floatToIntBits(2.2270844E38f) ^ 0x7F278C16)) {
                    this.fakePop((Entity)this.fakePlayer);
                    this.fakePlayer.setHealth(Float.intBitsToFloat(Float.floatToIntBits(0.41757607f) ^ 0x7F75CC88));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive receive) {
        block1: {
            double damage;
            SPacketExplosion explosion;
            if (this.fakePlayer == null) {
                return;
            }
            if (!(receive.getPacket() instanceof SPacketExplosion) || !(this.fakePlayer.getDistance((explosion = (SPacketExplosion)receive.getPacket()).getX(), explosion.getY(), explosion.getZ()) <= Double.longBitsToDouble(Double.doubleToLongBits(1.2157535937387267) ^ 0x7FDD73BA0A51A307L)) || !((damage = (double) DamageUtil.calculateDamage(explosion.getX(), explosion.getY(), explosion.getZ(), (EntityLivingBase)this.fakePlayer)) > Double.longBitsToDouble(Double.doubleToLongBits(2.2285043487829035E307) ^ 0x7FBFBC26084B0ECFL)) || !pops.getValue()) break block1;
            this.fakePlayer.setHealth((float)((double)this.fakePlayer.getHealth() - MathHelper.clamp((double)damage, (double)Double.longBitsToDouble(Double.doubleToLongBits(8.687006971746397E307) ^ 0x7FDEED40E3DDD5C1L), (double)Double.longBitsToDouble(Double.doubleToLongBits(0.002305631539810083) ^ 0x7FEDDB4281EC6CB5L))));
        }
    }

    public void fakePop(Entity entity) {
        FakePlayer.mc.effectRenderer.emitParticleAtEntity((Entity)entity, EnumParticleTypes.TOTEM, 30);
        FakePlayer.mc.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), Float.intBitsToFloat(Float.floatToIntBits(5.1085615f) ^ 0x7F237956), Float.intBitsToFloat(Float.floatToIntBits(16.397932f) ^ 0x7E032EF7), false);
    }

    public void spawnSinglePlayer() {
        block0: {
            this.fakePlayer = new EntityOtherPlayerMP((World) FakePlayer.mc.world, new GameProfile(FakePlayer.mc.player.getUniqueID(), "FakePlayer"));
            this.fakePlayer.copyLocationAndAnglesFrom((Entity) FakePlayer.mc.player);
            this.fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
            FakePlayer.mc.world.addEntityToWorld(-100, (Entity)this.fakePlayer);
            if (!copyInv.getValue()) break block0;
            this.fakePlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
        }
    }


}
