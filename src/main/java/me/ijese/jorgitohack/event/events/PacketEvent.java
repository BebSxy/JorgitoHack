package me.ijese.jorgitohack.event.events;

import me.ijese.jorgitohack.event.EventStage;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.sql.Time;

public class PacketEvent
        extends EventStage {
    private final Packet<?> packet;
    public Time time;

    public PacketEvent(Packet<?> packet, Time time) {
        this.packet = packet;
        this.time = time;
    }

    public PacketEvent(int stage, Packet<?> packet) {
        super(stage);
        this.packet = packet;
    }

    public Time getTime() {
        return this.time;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    @Cancelable
    public static class Send
            extends PacketEvent {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Receive
            extends PacketEvent {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    public static enum Time {
        Send,
        Receive;

    }
}
