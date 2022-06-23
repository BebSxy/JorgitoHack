package me.ijese.jorgitohack.features.modules.player;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.event.events.PacketEvent;
import me.ijese.jorgitohack.event.events.UpdateWalkingPlayerEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class Godmode
        extends Module {
    private final Setting<Boolean> remount = this.register(new Setting<Boolean>("Remount", false));
    public Minecraft mc = Minecraft.getMinecraft();
    public Entity entity;

    public Godmode() {
        super("Godmode", "Hi there :D", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.mc.world != null && this.mc.player.getRidingEntity() != null) {
            this.entity = this.mc.player.getRidingEntity();
            this.mc.renderGlobal.loadRenderers();
            this.hideEntity();
            this.mc.player.setPosition(Minecraft.getMinecraft().player.getPosition().getX(), Minecraft.getMinecraft().player.getPosition().getY() - 1, Minecraft.getMinecraft().player.getPosition().getZ());
        }
        if (this.mc.world != null && this.remount.getValue().booleanValue()) {
            this.remount.setValue(false);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.remount.getValue().booleanValue()) {
            this.remount.setValue(false);
        }
        this.mc.player.dismountRidingEntity();
        this.mc.getConnection().sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.mc.getConnection().sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            event.setCanceled(true);
        }
    }

    private void hideEntity() {
        if (this.mc.player.getRidingEntity() != null) {
            this.mc.player.dismountRidingEntity();
            this.mc.world.removeEntity(this.entity);
        }
    }

    private void showEntity(Entity entity2) {
        entity2.isDead = false;
        this.mc.world.loadedEntityList.add(entity2);
        this.mc.player.startRiding(entity2, true);
    }

    @SubscribeEvent
    public void onPlayerWalkingUpdate(UpdateWalkingPlayerEvent event) {
        if (this.entity == null) {
            return;
        }
        if (event.getStage() == 0) {
            if (this.remount.getValue().booleanValue() && Objects.requireNonNull(JorgitoHack.moduleManager.getModuleByClass(Godmode.class)).isEnabled()) {
                this.showEntity(this.entity);
            }
            this.entity.setPositionAndRotation(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ, Minecraft.getMinecraft().player.rotationYaw, Minecraft.getMinecraft().player.rotationPitch);
            this.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.mc.player.rotationYaw, this.mc.player.rotationPitch, true));
            this.mc.player.connection.sendPacket(new CPacketInput(this.mc.player.movementInput.moveForward, this.mc.player.movementInput.moveStrafe, false, false));
            this.mc.player.connection.sendPacket(new CPacketVehicleMove(this.entity));
        }
    }
}