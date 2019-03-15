package de.mcharvest.saith.commands;

import de.mcharvest.saith.Main;
import de.mcharvest.saith.nav.DestinationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DestinationCommand implements CommandExecutor {
    private DestinationManager destinationManager = Main.getInstance().getDestinationManager();

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (args.length == 0) {
                sendUsage(p);
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("navigator.destination.set")) {
                    setDestination(p, args);
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (p.hasPermission("navgator.destination.add")) {
                    addCheckpoint(p, args);
                }
            }

        }
        return true;
    }

    //TODO
    //Sends the player a message how to use the command.
    private void sendUsage(Player p) {
        p.sendMessage("");
    }

    //Creates a new Location if it doesn't exist
    private void setDestination(Player p, String[] args) {
        if (args.length == 2) {
            String destinationName = args[1];

            if (destinationManager.destinationExists(destinationName)) {
                p.sendMessage("§4Destination already exists.");
            } else {
                try {
                    destinationManager.createNewDestination(destinationName, p.getLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                p.sendMessage("§aDestination successfully set.");
            }
        } else {
            sendUsage(p);
        }
    }

    private void addCheckpoint(Player p, String[] args) {
        try {
            destinationManager.createNewCheckPoint(p.getLocation());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        p.sendMessage("§aCheckpoint successfully set.");

    }
}
