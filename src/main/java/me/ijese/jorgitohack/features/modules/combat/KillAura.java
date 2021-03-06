package me.ijese.jorgitohack.features.modules.combat;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.event.events.UpdateWalkingPlayerEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillAura
        extends Module {
    public static Entity target;
    private final Timer timer = new Timer();
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f)));
    public Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("AutoSwitch", false));
    public Setting<Boolean> delay = this.register(new Setting<Boolean>("Delay", true));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", true));
    public Setting<Boolean> stay = this.register(new Setting<Object>("Stay", Boolean.valueOf(true), v -> this.rotate.getValue()));
    public Setting<Boolean> armorBreak = this.register(new Setting<Boolean>("ArmorBreak", false));
    public Setting<Boolean> eating = this.register(new Setting<Boolean>("Eating", true));
    public Setting<Boolean> onlySharp = this.register(new Setting<Boolean>("Axe/Sword", true));
    public Setting<Boolean> teleport = this.register(new Setting<Boolean>("Teleport", false));
    public Setting<Float> raytrace = this.register(new Setting<Object>("Raytrace", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(7.0f), v -> this.teleport.getValue() == false, "Wall Range."));
    public Setting<Float> teleportRange = this.register(new Setting<Object>("TpRange", Float.valueOf(15.0f), Float.valueOf(0.1f), Float.valueOf(50.0f), v -> this.teleport.getValue(), "Teleport Range."));
    public Setting<Boolean> lagBack = this.register(new Setting<Object>("LagBack", Boolean.valueOf(true), v -> this.teleport.getValue()));
    public Setting<Boolean> teekaydelay = this.register(new Setting<Boolean>("32kDelay", false));
    public Setting<Integer> time32k = this.register(new Setting<Integer>("32kTime", 5, 1, 50));
    public Setting<Integer> multi = this.register(new Setting<Object>("32kPackets", Integer.valueOf(2), v -> this.teekaydelay.getValue() == false));
    public Setting<Boolean> multi32k = this.register(new Setting<Boolean>("Multi32k", false));
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> mobs = this.register(new Setting<Boolean>("Mobs", false));
    public Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", false));
    public Setting<Boolean> vehicles = this.register(new Setting<Boolean>("Entities", false));
    public Setting<Boolean> projectiles = this.register(new Setting<Boolean>("Projectiles", false));
    public Setting<Boolean> tps = this.register(new Setting<Boolean>("TpsSync", true));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", false));
    public Setting<Boolean> swing = this.register(new Setting<Boolean>("Swing", true));
    public Setting<Boolean> sneak = this.register(new Setting<Boolean>("State", false));
    public Setting<Boolean> info = this.register(new Setting<Boolean>("Info", true));
    private final Setting<TargetMode> targetMode = this.register(new Setting<TargetMode>("Target", TargetMode.CLOSEST));
    public Setting<Float> health = this.register(new Setting<Object>("Health", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.targetMode.getValue() == TargetMode.SMART));

    public KillAura() {
        super("NewKillaura", "Kills aura.", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (!this.rotate.getValue().booleanValue()) {
            this.doNewKillaura();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotate.getValue().booleanValue()) {
            if (this.stay.getValue().booleanValue() && target != null) {
                JorgitoHack.rotationManager.lookAtEntity(target);
            }
            this.doNewKillaura();
        }
    }

    private void doNewKillaura() {
        int sword;
        if (this.onlySharp.getValue().booleanValue() && !EntityUtil.holdingWeapon(KillAura.mc.player)) {
            target = null;
            return;
        }
        int wait = this.delay.getValue() == false || EntityUtil.holding32k(KillAura.mc.player) && this.teekaydelay.getValue() == false ? 0 : (wait = (int) ((float) DamageUtil.getCooldownByWeapon(KillAura.mc.player) * (this.tps.getValue() != false ? JorgitoHack.serverManager.getTpsFactor() : 1.0f)));
        if (!this.timer.passedMs(wait) || !this.eating.getValue().booleanValue() && KillAura.mc.player.isHandActive() && (!KillAura.mc.player.getHeldItemOffhand().getItem().equals(Items.SHIELD) || KillAura.mc.player.getActiveHand() != EnumHand.OFF_HAND)) {
            return;
        }
        if (!(this.targetMode.getValue() == TargetMode.FOCUS && target != null && (KillAura.mc.player.getDistanceSq(target) < MathUtil.square(this.range.getValue().floatValue()) || this.teleport.getValue().booleanValue() && KillAura.mc.player.getDistanceSq(target) < MathUtil.square(this.teleportRange.getValue().floatValue())) && (KillAura.mc.player.canEntityBeSeen(target) || EntityUtil.canEntityFeetBeSeen(target) || KillAura.mc.player.getDistanceSq(target) < MathUtil.square(this.raytrace.getValue().floatValue()) || this.teleport.getValue().booleanValue()))) {
            target = this.getTarget();
        }
        if (target == null) {
            return;
        }
        if (this.autoSwitch.getValue().booleanValue() && (sword = InventoryUtil.findHotbarBlock(ItemSword.class)) != -1) {
            InventoryUtil.switchToHotbarSlot(sword, false);
        }
        if (this.rotate.getValue().booleanValue()) {
            JorgitoHack.rotationManager.lookAtEntity(target);
        }
        if (this.teleport.getValue().booleanValue()) {
            JorgitoHack.positionManager.setPositionPacket(KillAura.target.posX, EntityUtil.canEntityFeetBeSeen(target) ? KillAura.target.posY : KillAura.target.posY + (double) target.getEyeHeight(), KillAura.target.posZ, true, true, this.lagBack.getValue() == false);
        }
        if (EntityUtil.holding32k(KillAura.mc.player) && !this.teekaydelay.getValue().booleanValue()) {
            if (this.multi32k.getValue().booleanValue()) {
                for (EntityPlayer player : KillAura.mc.world.playerEntities) {
                    if (!EntityUtil.isValid(player, this.range.getValue().floatValue())) continue;
                    this.teekayAttack(player);
                }
            } else {
                this.teekayAttack(target);
            }
            this.timer.reset();
            return;
        }
        if (this.armorBreak.getValue().booleanValue()) {
            KillAura.mc.playerController.windowClick(KillAura.mc.player.inventoryContainer.windowId, 9, KillAura.mc.player.inventory.currentItem, ClickType.SWAP, KillAura.mc.player);
            EntityUtil.attackEntity(target, this.packet.getValue(), this.swing.getValue());
            KillAura.mc.playerController.windowClick(KillAura.mc.player.inventoryContainer.windowId, 9, KillAura.mc.player.inventory.currentItem, ClickType.SWAP, KillAura.mc.player);
            EntityUtil.attackEntity(target, this.packet.getValue(), this.swing.getValue());
        } else {
            boolean sneaking = KillAura.mc.player.isSneaking();
            boolean sprint = KillAura.mc.player.isSprinting();
            if (this.sneak.getValue().booleanValue()) {
                if (sneaking) {
                    KillAura.mc.player.connection.sendPacket(new CPacketEntityAction(KillAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (sprint) {
                    KillAura.mc.player.connection.sendPacket(new CPacketEntityAction(KillAura.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
            EntityUtil.attackEntity(target, this.packet.getValue(), this.swing.getValue());
            if (this.sneak.getValue().booleanValue()) {
                if (sprint) {
                    KillAura.mc.player.connection.sendPacket(new CPacketEntityAction(KillAura.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (sneaking) {
                    KillAura.mc.player.connection.sendPacket(new CPacketEntityAction(KillAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
            }
        }
        this.timer.reset();
    }

    private void teekayAttack(Entity entity) {
        for (int i = 0; i < this.multi.getValue(); ++i) {
            this.startEntityAttackThread(entity, i * this.time32k.getValue());
        }
    }

    private void startEntityAttackThread(Entity entity, int time) {
        new Thread(() -> {
            Timer timer = new Timer();
            timer.reset();
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            EntityUtil.attackEntity(entity, true, this.swing.getValue());
        }).start();
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = this.teleport.getValue() != false ? (double) this.teleportRange.getValue().floatValue() : (double) this.range.getValue().floatValue();
        double maxHealth = 36.0;
        for (Entity entity : KillAura.mc.world.loadedEntityList) {
            if (!(this.players.getValue() != false && entity instanceof EntityPlayer || this.animals.getValue() != false && EntityUtil.isPassive(entity) || this.mobs.getValue() != false && EntityUtil.isMobAggressive(entity) || this.vehicles.getValue() != false && EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue().booleanValue() || !EntityUtil.isProjectile(entity)) || entity instanceof EntityLivingBase && EntityUtil.isntValid(entity, distance) || !this.teleport.getValue().booleanValue() && !KillAura.mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && KillAura.mc.player.getDistanceSq(entity) > MathUtil.square(this.raytrace.getValue().floatValue()))
                continue;
            if (target == null) {
                target = entity;
                distance = KillAura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer) entity, 18)) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < this.health.getValue().floatValue()) {
                target = entity;
                break;
            }
            if (this.targetMode.getValue() != TargetMode.HEALTH && KillAura.mc.player.getDistanceSq(entity) < distance) {
                target = entity;
                distance = KillAura.mc.player.getDistanceSq(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (this.targetMode.getValue() != TargetMode.HEALTH || !((double) EntityUtil.getHealth(entity) < maxHealth))
                continue;
            target = entity;
            distance = KillAura.mc.player.getDistanceSq(entity);
            maxHealth = EntityUtil.getHealth(entity);
        }
        return target;
    }

    @Override
    public String getDisplayInfo() {
        if (this.info.getValue().booleanValue() && target instanceof EntityPlayer) {
            return target.getName();
        }
        return null;
    }

    public enum TargetMode {
        FOCUS,
        CLOSEST,
        HEALTH,
        SMART

    }
}
