package me.ijese.jorgitohack.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.ijese.jorgitohack.JorgitoHack;
import me.ijese.jorgitohack.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : JorgitoHack.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + JorgitoHack.commandManager.getPrefix() + command.getName());
        }
    }
}

