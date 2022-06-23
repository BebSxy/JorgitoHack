package me.ijese.jorgitohack.features.modules.movement;

import me.ijese.jorgitohack.event.events.PlayerMoveEvent;
import me.ijese.jorgitohack.features.modules.Module;
import me.ijese.jorgitohack.features.setting.Setting;
import me.ijese.jorgitohack.util.MotionUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer;

public class ClipFlight extends Module {
    public ClipFlight(){
        super("ClipFlight", "", Category.MOVEMENT, true, false, false);
    }
    public enum Settings{
        Clip,
        Flight
    }
    public Setting<Settings> flight = register(new Setting("Flight", Settings.Clip));
    public Setting<Integer> packets = register(new Setting("Packets", 80, 1, 300));
    public Setting<Integer> speed = register(new Setting("XZ Speed", 7, -99, 99, v -> flight.getValue() == Settings.Flight));
    public Setting<Integer> speedY = register(new Setting("Y Speed", 7, -99, 99));
    public Setting<Boolean> bypass = register(new Setting("Bypass", false));
    public Setting<Integer> interval = register(new Setting("Interval", 25,1,100, v -> flight.getValue() == Settings.Clip));
    public Setting<Boolean> update = register(new Setting("Update Position Client Side", false));

    int num = 0;

    double startFlat = 0;

    public void onEnable() {
        startFlat = mc.player.posY;
        num = 0;
    }

    @Override
    public void onUpdate() {

        double[] dir = MotionUtil.forward(speed.getValue());

        double yposition;

        switch (flight.getValue()) {

            case Flight:
                double xPos = mc.player.posX;
                double yPos = mc.player.posY;
                double zPos = mc.player.posZ;

                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown())
                    yPos += speedY.getValue();
                else if (mc.gameSettings.keyBindSneak.isKeyDown())
                    yPos -= speedY.getValue();

                xPos += dir[0];
                zPos += dir[1];

                mc.player.connection.sendPacket(new CPacketPlayer.Position(xPos, yPos, zPos, false));
                if (update.getValue())
                    mc.player.setPosition(xPos, yPos, zPos);
                if (bypass.getValue())
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY + 0.05, mc.player.posZ, true));

                break;


            case Clip:

                if (mc.gameSettings.keyBindSprint.isKeyDown() || mc.player.ticksExisted % interval.getValue() == 0) {
                    for (int i = 0; i < packets.getValue(); i++) {

                        yposition = mc.player.posY + speedY.getValue();

                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, yposition, mc.player.posZ, false));
                        if (update.getValue()) mc.player.setPosition(mc.player.posX, yposition, mc.player.posZ);
                        if (bypass.getValue())
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY + 0.05, mc.player.posZ, true));

                    }
                }


                break;

        }

    }
}
