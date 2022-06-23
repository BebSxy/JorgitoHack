package me.ijese.jorgitohack.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.ijese.jorgitohack.features.command.Command;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class CoordsCommand extends Command {
    public CoordsCommand() {
        super("coords", new String[]{"coords", "CopyCoords", "CopyPos", "CopyPosition"});
    }

    @Override
    public void execute(String[] commands) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection("I am here right now!, X: " + Math.round(CoordsCommand.mc.player.posX) + ", " + "Y: " + Math.round(CoordsCommand.mc.player.posY) + ", " + "Z: " +Math.round(CoordsCommand.mc.player.posZ) + ".");
        clipboard.setContents(stringSelection, null);
        Command.sendMessage(ChatFormatting.BOLD + "Copied coords to clipboard!");
    }
}
