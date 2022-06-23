package me.ijese.jorgitohack.event.events;

import me.ijese.jorgitohack.event.EventStage;
import net.minecraft.entity.player.EntityPlayer;

public
class PopEvent
        extends EventStage {
    private final EntityPlayer entity;

    public
    PopEvent(EntityPlayer entity) {
        this.entity = entity;
    }

    public
    EntityPlayer getEntity() {
        return this.entity;
    }
}
