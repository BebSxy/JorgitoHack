package me.ijese.jorgitohack.features.command.commands;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        JorgitoHack.unload(true);
    }
}

