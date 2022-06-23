package me.ijese.jorgitohack.features.command.commands;

import me.ijese.jorgitohack.features.command.Command;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class NamemcCommand
        extends Command {
    public NamemcCommand() {
        super("namemc", new String[]{"<NameMc>", "<name>"});
    }

    @Override
    public void execute(String[] commands) {
        String name = commands[0];
        try {
            Desktop.getDesktop().browse(URI.create("https://namemc.com/search?q=" + name));
        }
        catch (IOException var4) {
            var4.printStackTrace();
        }
    }
}