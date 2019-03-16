package de.mcharvest.saith.commands;

import de.mcharvest.saith.Main;
import de.mcharvest.saith.nav.Navigator;
import de.mcharvest.saith.nav.PathVisualizer;
import de.mcharvest.saith.nav.destination.IDestinationManager;
import de.mcharvest.saith.nav.dijkstra.Vertex;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class DestinationCommand implements CommandExecutor {
    private IDestinationManager destinationManager = Main.getInstance().getDestinationManager();

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
            } else if (args[0].equalsIgnoreCase("find")) {
                if (p.hasPermission("navgator.destination.find")) {
                    showRoute(p, args);

                }
            } else if (args[0].equalsIgnoreCase("ride")) {
                if (p.hasPermission("navigator.destination.ride")) {

                }
            } else if (args[0].equalsIgnoreCase("show")) {
                if (p.hasPermission("navigator.destination.show")) {
                    Main.getInstance().getNavigator().showGraphToPlayer(p);
                }
            } else if(args[0].equalsIgnoreCase("list")){
                if(p.hasPermission("navigator.destination.list")){
                    Stream.of(destinationManager.getDestinationsName()).forEach(p::sendMessage);
                }
            }

        }
        return true;
    }

    //TODO
    //Sends the player a message how to use the command.
    private void sendUsage(Player p) {
        p.sendMessage("Newds");
    }

    //Creates a new Location if it doesn't exist
    private void setDestination(Player p, String[] args) {
        if (args.length == 2) {
            String destinationName = args[1];

            if (destinationManager.destinationExists(destinationName)) {
                p.sendMessage("§4Destination already exists.");
            } else {
                destinationManager.createNewDestination(destinationName, p.getLocation());
                p.sendMessage("§aDestination successfully set.");
            }
        } else {
            sendUsage(p);
        }
    }

    private void addCheckpoint(Player p, String[] args) {
        if (destinationManager.checkPointExists(p.getLocation())) {
            p.sendMessage("§4Checkpoint already exists.");
        } else {
            destinationManager.createNewCheckPoint(p.getLocation());
            p.sendMessage("§aCheckpoint successfully set.");
        }

    }

    private void showRoute(Player p, String[] args) {
        if (args.length == 2) {
            String destinationName = args[1];

            if (!destinationManager.destinationExists(destinationName)) {
                p.sendMessage("§4Destination doesn't exists.");
            } else {
                Vertex[] path = getPath(p, destinationName);
                if(path == null){
                    p.sendMessage("§4Sorry. But I couldnt find a path to your desired destination.");
                    return;
                }
                PathVisualizer.showGraphToPlayer(p, path);
            }
        } else {
            sendUsage(p);
        }
    }

    private void ridePigToDestination(Player p, String[] args) {
        if (args.length == 2) {
            String destinationName = args[1];

            if (!destinationManager.destinationExists(destinationName)) {
                p.sendMessage("§4Destination doesn't exists.");
            } else {
                Vertex[] path = getPath(p, destinationName);
                if(path == null){
                    p.sendMessage("§4Sorry. But I couldnt find a path to your desired destination.");
                    return;
                }
                //Todo: Implement Riding pig
            }
        } else {
            sendUsage(p);
        }
    }

    private Vertex[] getPath(Player p, String destinationName) {
        Location destination = destinationManager.getDestinationLocation(destinationName);
        Vertex[] path = Main.getInstance().getNavigator().findShortestPath(p.getLocation(), destination);
        if (path == null) {

            return null;
        }
        return path;
    }
}
