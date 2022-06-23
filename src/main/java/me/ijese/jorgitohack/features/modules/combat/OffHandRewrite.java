package me.ijese.jorgitohack.features.modules.combat;

import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.Entity;
import me.ijese.jorgitohack.util.EntityUtil;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import me.ijese.jorgitohack.features.Feature;
import me.ijese.jorgitohack.event.events.PacketEvent;
import org.lwjgl.input.Mouse;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import me.ijese.jorgitohack.event.events.ProcessRightClickBlockEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.Timer;
import me.ijese.jorgitohack.util.InventoryUtil;
import java.util.Queue;
import me.ijese.jorgitohack.features.modules.Module;
//from xulu+
public class OffHandRewrite extends Module
{
    private static OffHandRewrite instance;
    private final Queue<InventoryUtil.Task> taskList;
    private final Timer timer;
    private final Timer secondTimer;
    public Setting<Boolean> crystal;
    public Setting<Float> crystalHealth;
    public Setting<Float> crystalHoleHealth;
    public Setting<Boolean> gapple;
    public Setting<Boolean> armorCheck;
    public Setting<Integer> actions;
    public Mode2 currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private boolean second;
    private boolean switchedForHealthReason;

    public OffHandRewrite() {
        super("OffHandRewrite", "Allows you to switch up your Offhand.", Category.COMBAT, true, false, false);
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.crystal = (Setting<Boolean>)this.register(new Setting("Crystal", true));
        this.crystalHealth = (Setting<Float>)this.register(new Setting("CrystalHP", 13.0f, 0.1f, 36.0f));
        this.crystalHoleHealth = (Setting<Float>)this.register(new Setting("CrystalHoleHP", 3.5f, 0.1f, 36.0f));
        this.gapple = (Setting<Boolean>)this.register(new Setting("Gapple", true));
        this.armorCheck = (Setting<Boolean>)this.register(new Setting("ArmorCheck", true));
        this.actions = (Setting<Integer>)this.register(new Setting("Actions", 4, 1, 4));
        this.currentMode = Mode2.TOTEMS;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        OffHandRewrite.instance = this;
    }

    public static OffHandRewrite getInstance() {
        if (OffHandRewrite.instance == null) {
            OffHandRewrite.instance = new OffHandRewrite();
        }
        return OffHandRewrite.instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffHandRewrite.mc.objectMouseOver != null && event.pos == OffHandRewrite.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            OffHandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
            OffHandRewrite.mc.playerController.processRightClick((EntityPlayer) OffHandRewrite.mc.player, (World) OffHandRewrite.mc.world, EnumHand.OFF_HAND);
        }
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (OffHandRewrite.mc.player != null && OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffHandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                OffHandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
                OffHandRewrite.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        }
        else if (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffHandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            OffHandRewrite.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!Feature.fullNullCheck() && OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OffHandRewrite.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && OffHandRewrite.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet2 = event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        OffHandRewrite.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        OffHandRewrite.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            }
            else if (event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem) event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystals";
        }
        if (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totems";
        }
        if (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapples";
        }
        return null;
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.holdingTotem = (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
        this.holdingGapple = (OffHandRewrite.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
        this.totems = OffHandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.holdingTotem) {
            this.totems += OffHandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        }
        this.crystals = OffHandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (this.holdingCrystal) {
            this.crystals += OffHandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        }
        this.gapples = OffHandRewrite.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (this.holdingGapple) {
            this.gapples += OffHandRewrite.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        }
        this.doSwitch();
    }

    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.gapple.getValue() && OffHandRewrite.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && OffHandRewrite.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.currentMode = Mode2.GAPPLES;
        }
        else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue() && ((EntityUtil.isSafe((Entity) OffHandRewrite.mc.player) && EntityUtil.getHealth((Entity) OffHandRewrite.mc.player, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity) OffHandRewrite.mc.player, true) > this.crystalHealth.getValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity) OffHandRewrite.mc.player) && EntityUtil.getHealth((Entity) OffHandRewrite.mc.player, true) <= this.crystalHealth.getValue()) || EntityUtil.getHealth((Entity) OffHandRewrite.mc.player, true) <= this.crystalHoleHealth.getValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity) OffHandRewrite.mc.player) && EntityUtil.getHealth((Entity) OffHandRewrite.mc.player, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity) OffHandRewrite.mc.player, true) > this.crystalHealth.getValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue() && (OffHandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || OffHandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || OffHandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || OffHandRewrite.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Mode2.TOTEMS);
        }
        if (OffHandRewrite.mc.currentScreen instanceof GuiContainer && !(OffHandRewrite.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        final Item currentOffhandTolonEditionItem = OffHandRewrite.mc.player.getHeldItemOffhand().getItem();
        switch (this.currentMode) {
            case TOTEMS: {
                if (this.totems <= 0) {
                    break;
                }
                if (this.holdingTotem) {
                    break;
                }
                this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                final int lastSlot = this.getLastSlot(currentOffhandTolonEditionItem, this.lastTotemSlot);
                this.putItemInOffhandTolonEdition(this.lastTotemSlot, lastSlot);
                break;
            }
            case GAPPLES: {
                if (this.gapples <= 0) {
                    break;
                }
                if (this.holdingGapple) {
                    break;
                }
                this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                final int lastSlot = this.getLastSlot(currentOffhandTolonEditionItem, this.lastGappleSlot);
                this.putItemInOffhandTolonEdition(this.lastGappleSlot, lastSlot);
                break;
            }
            default: {
                if (this.crystals <= 0) {
                    break;
                }
                if (this.holdingCrystal) {
                    break;
                }
                this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                final int lastSlot = this.getLastSlot(currentOffhandTolonEditionItem, this.lastCrystalSlot);
                this.putItemInOffhandTolonEdition(this.lastCrystalSlot, lastSlot);
                break;
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            final InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
                task.run();
                if (task.isSwitching()) {
                    this.didSwitchThisTick = true;
                }
            }
        }
    }

    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }

    private void putItemInOffhandTolonEdition(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    public void setMode(final Mode2 mode) {
        this.currentMode = ((this.currentMode == mode) ? Mode2.TOTEMS : mode);
    }

    public enum Mode2
    {
        TOTEMS,
        GAPPLES,
        CRYSTALS;
    }
}
