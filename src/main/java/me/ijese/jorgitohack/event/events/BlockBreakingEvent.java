package me.ijese.jorgitohack.event.events;

import me.ijese.jorgitohack.event.EventStage;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingEvent
        extends EventStage {
    public BlockPos pos;
    public int breakingID;
    public int breakStage;

    public BlockBreakingEvent(BlockPos pos, int breakingID, int breakStage) {
        this.pos = pos;
        this.breakingID = breakingID;
        this.breakStage = breakStage;
    }
}
