package de.mcharvest.saith.commands;

import de.mcharvest.saith.Main;
import de.mcharvest.saith.listeners.CheckPointListener;
import de.mcharvest.saith.nav.AnimalCap;
import de.mcharvest.saith.nav.NavigationManager;
import de.mcharvest.saith.nav.PathVisualizer;
import de.mcharvest.saith.nav.destination.IDestinationManager;
import de.mcharvest.saith.nav.dijkstra.Vertex;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class DestinationCommand implements CommandExecutor {
    private IDestinationManager destinationManager = Main.getInstance().getDestinationManager();
    private NavigationManager navigationManager = Main.getInstance().getNavigationManager();

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (args.length == 0) {
                sendUsage(p);
                return true;
            }
            if(args[0].equalsIgnoreCase("remove")){
                if(p.hasPermission("navigator.destination.remove")){
                    remove(p,args);
                }
            }else if (args[0].equalsIgnoreCase("create")) {
                if (p.hasPermission("navigator.destination.create")) {
                    createMap(p, args);
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("navigator.destination.set")) {
                    setDestination(p, args);
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (p.hasPermission("navgator.destination.add")) {
                    checkPointAdd(p, args);
                }
            } else if (args[0].equalsIgnoreCase("find") ||
                    args[0].equalsIgnoreCase("findblock")) {
                if (p.hasPermission("navgator.destination.find")) {
                    showRoute(p, args);
                }
            } else if (args[0].equalsIgnoreCase("ride")) {
                if (p.hasPermission("navigator.destination.ride")) {
                    ridePigToDestination(p, args);
                }
            } else if (args[0].equalsIgnoreCase("show")) {
                if (p.hasPermission("navigator.destination.show")) {
                    // navigationManager.getNavigator("").showGraphToPlayer(p);
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (p.hasPermission("navigator.destination.list")) {
                    listDestinations(p, args);
                }
            }

        }
        return true;
    }

    private void remove(Player p, String[] args) {
        if(args.length >= 2){
            String mapName = args[1];
            if(!destinationManager.mapExists(mapName)){
                p.sendMessage(Main.getPrefix()+"§4Map doesn't exists.");
                return;
            }
            if(args.length == 2){
                destinationManager.removeMap(mapName);
                p.sendMessage(Main.getPrefix()+"§4Map removed.");

            }else if(args.length == 3){
                String destinationName = args[2];
                if(destinationManager.destinationExists(mapName,destinationName)){
                    p.sendMessage(Main.getPrefix()+"§4Destination doesn't exists.");
                }else{
                    destinationManager.removeDestination(mapName,destinationName);
                    p.sendMessage(Main.getPrefix()+"§4Destination removed.");
                }
            }
        }
    }


    private void checkPointAdd(Player p, String[] args) {
        if (CheckPointListener.isInCheckpointAddMode(p)) {
            CheckPointListener.disableCheckpointAddMode(p);
            p.sendMessage(Main.getPrefix()+"§4CheckPointAddMode disabled.");
        } else {
            if (args.length == 2) {
                String mapName = args[1];
                if (!destinationManager.mapExists(mapName)) {
                    p.sendMessage(Main.getPrefix()+"§4Map doesn't exists.");
                } else {
                    CheckPointListener.enableCheckpointAddMode(p, mapName);
                    p.sendMessage(Main.getPrefix()+"§aCheckPointAddMode enabled.");
                }

            }
        }
    }

    private void createMap(Player p, String[] args) {
        if (args.length == 2) {
            String mapName = args[1];
            if (destinationManager.mapExists(mapName)) {
                p.sendMessage(Main.getPrefix()+"§4Map already exists.");
            } else {
                destinationManager.createNewMap(mapName);
                p.sendMessage(Main.getPrefix()+"§aMap successfully created.");
            }
        }
    }

    private void listDestinations(Player p, String[] args) {
        if (args.length == 2) {
            String mapName = args[1];
            String[] destinations = destinationManager.getDestinationNames(mapName);
            for(String destination:destinations){
                p.sendMessage("- "+destination.substring(0,destination.length()-3));
            }
        } else {
            sendUsage(p);
        }
    }

    //TODO
    //Sends the player a message how to use the command.
    private void sendUsage(Player p) {
        p.sendMessage(Main.getPrefix()+"§4Wrong command synthax.");
    }

    //Creates a new Location if it doesn't exist
    private void setDestination(Player p, String[] args) {
        if (args.length == 3) {
            String mapName = args[1];
            String destinationName = args[2];

            if (destinationManager.destinationExists(mapName, destinationName)) {
                p.sendMessage(Main.getPrefix()+"§4Destination already exists.");
            } else {
                destinationManager.createNewDestination(mapName, destinationName, p.getLocation());
                p.sendMessage(Main.getPrefix()+"§aDestination successfully set.");
            }
        } else {
            sendUsage(p);
        }
    }


    private void showRoute(Player p, String[] args) {
        if (args.length == 3) {
            String mapName = args[1];
            String destinationName = args[2];

            if (!destinationManager.destinationExists(mapName, destinationName)) {
                p.sendMessage(Main.getPrefix()+"§4Destination doesn't exists.");
            } else {
                Vertex[] path = getPath(p, mapName, destinationName);
                if (path == null) {
                    p.sendMessage(Main.getPrefix()+"§4Sorry. But I couldnt find a path to your desired destination.");
                    p.sendMessage(Main.getPrefix()+"§4The destination §6" + destinationName + " §4has insufficent checkpoints.");
                    return;
                }
                if (args[0].equalsIgnoreCase("find")) {
                    PathVisualizer.showGraphToPlayer(p, path);
                } else {
                    PathVisualizer.showBlocksToPlayer(p, path);
                }
            }
        } else {
            sendUsage(p);
        }
    }

    private void ridePigToDestination(Player p, String[] args) {
        if (args.length == 3) {
            String mapName = args[1];
            String destinationName = args[2];

            if (!destinationManager.destinationExists(mapName, destinationName)) {
                p.sendMessage(Main.getPrefix()+"§4Destination doesn't exists.");
            } else {
                Vertex[] path = getPath(p, mapName, destinationName);
                if (path == null) {
                    p.sendMessage(Main.getPrefix()+"§4Sorry. But I couldnt find a path to your desired destination.");
                    return;
                }
                AnimalCap.rideEntityToDestination(p, path, Pig.class);
            }
        } else {
            sendUsage(p);
        }
    }


    private Vertex[] getPath(Player p, String mapName, String destinationName) {
        Location destination = destinationManager.getDestinationLocation(mapName, destinationName);
        Vertex[] path = navigationManager.getNavigator(mapName).findShortestPath(p.getLocation(), destination);
        if (path == null) {

            return null;
        }
        return path;
    }
}
