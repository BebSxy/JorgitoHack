package me.ijese.jorgitohack.features.command.commands;

import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        JorgitoHack.reload();
    }
}

