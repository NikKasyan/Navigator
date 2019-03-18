package de.mcharvest.saith.commands;

import de.mcharvest.saith.NavigatorPlugin;
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

public class DestinationCommand implements CommandExecutor {
    private IDestinationManager destinationManager = NavigatorPlugin.getInstance().getDestinationManager();
    private NavigationManager navigationManager = NavigatorPlugin.getInstance().getNavigationManager();

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (args.length == 0) {
                sendUsage(p);
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (p.hasPermission("navigator.destination.remove")) {
                    remove(p, args);
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("navigator.destination.reload")) {
                    reloadNavigationManager(p, args);
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (p.hasPermission("navigator.destination.create")) {
                    createMap(p, args);
                }
            } else if (args[0].equalsIgnoreCase("set")) {
                if (p.hasPermission("navigator.destination.set")) {
                    setDestination(p, args);
                }
            } else if (args[0].equalsIgnoreCase("edit")) {
                if (p.hasPermission("navigator.destination.edit")) {
                    checkPointEdit(p, args);
                }
            } else if (args[0].equalsIgnoreCase("find") ||
                    args[0].equalsIgnoreCase("findblock")) {
                if (p.hasPermission("navigator.destination.find")) {
                    showRoute(p, args);
                }
            } else if (args[0].equalsIgnoreCase("ride")) {
                if (p.hasPermission("navigator.destination.ride")) {
                    ridePigToDestination(p, args);
                }
            } else if (args[0].equalsIgnoreCase("show")) {
                if (p.hasPermission("navigator.destination.show")) {
                    showGrid(p, args);
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (p.hasPermission("navigator.destination.list")) {
                    listDestinations(p, args);
                }
            }

        }
        return true;
    }

    private void showGrid(Player p, String[] args) {
        if (args.length == 2) {
            String mapName = args[1];
            if (!destinationManager.mapExists(mapName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Map doesn't exists.");
                return;
            }
            navigationManager.getNavigator(mapName).
                    showBlockGraphToPlayer(p,
                            destinationManager.getDestinations(mapName),
                            destinationManager.getCheckpoints(mapName));
            p.sendMessage(NavigatorPlugin.getPrefix() + "§aThis is the Layout of your Map.");
        }
    }

    private void reloadNavigationManager(Player p, String[] args) {
        if (args.length == 1) {
            navigationManager.reload();
            p.sendMessage(NavigatorPlugin.getPrefix() + "§aAll maps have been reloaded.");
        } else if (args.length == 2) {
            String mapName = args[1];
            if (!destinationManager.mapExists(mapName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Map doesn't exists.");
                return;
            }
            navigationManager.reload(mapName);
            p.sendMessage(NavigatorPlugin.getPrefix() + "§aMap §6" + mapName + " §ahas been reloaded");
        }
    }

    private void remove(Player p, String[] args) {
        if (args.length >= 2) {
            String mapName = args[1];
            if (!destinationManager.mapExists(mapName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Map doesn't exists.");
                return;
            }
            if (args.length == 2) {
                destinationManager.removeMap(mapName);
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Map removed.");
                if (NavigatorPlugin.getNavConfig().auto_reload) {
                    reloadNavigationManager(p, args);
                }

            } else if (args.length == 3) {
                String destinationName = args[2];
                if (destinationManager.destinationExists(mapName, destinationName)) {
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§4Destination doesn't exists.");
                } else {
                    destinationManager.removeDestination(mapName, destinationName);
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§4Destination removed.");
                    if (NavigatorPlugin.getNavConfig().auto_reload) {
                        reloadNavigationManager(p, args);
                    }
                }
            }
        }
    }


    private void checkPointEdit(Player p, String[] args) {
        if (CheckPointListener.isInCheckpointEditMode(p)) {
            CheckPointListener.disableCheckpointEditMode(p);
            p.sendMessage(NavigatorPlugin.getPrefix() + "§4EditCheckPointMode disabled.");
            p.sendMessage(NavigatorPlugin.getPrefix() + "§4All placed GoldBlocks have been removed.");
            if (NavigatorPlugin.getNavConfig().auto_reload) {
                reloadNavigationManager(p, args);
            }
        } else {
            if (args.length == 2) {
                String mapName = args[1];
                if (!destinationManager.mapExists(mapName)) {
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§4Map doesn't exists.");
                } else {
                    CheckPointListener.enableCheckpointEditMode(p, mapName);
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§aEditCheckPointMode enabled.");
                }

            }
        }
    }

    private void createMap(Player p, String[] args) {
        if (args.length == 2) {
            String mapName = args[1];
            if (destinationManager.mapExists(mapName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Map already exists.");
            } else {
                destinationManager.createNewMap(mapName);
                p.sendMessage(NavigatorPlugin.getPrefix() + "§aMap successfully created.");
                if (NavigatorPlugin.getNavConfig().auto_reload) {
                    reloadNavigationManager(p, args);
                }
            }
        }
    }

    private void listDestinations(Player p, String[] args) {
        if (args.length == 2) {
            String mapName = args[1];
            String[] destinations = destinationManager.getDestinationNames(mapName);
            for (String destination : destinations) {
                p.sendMessage("- " + destination.substring(0, destination.length() - 4));
            }
        } else {
            sendUsage(p);
        }
    }

    //TODO
    //Sends the player a message how to use the command.
    private void sendUsage(Player p) {
        p.sendMessage(NavigatorPlugin.getPrefix() + "§4Wrong command syntax.");
    }

    //Creates a new Destination if it doesn't exist
    private void setDestination(Player p, String[] args) {
        if (args.length == 3) {
            String mapName = args[1];
            String destinationName = args[2];

            if (destinationManager.destinationExists(mapName, destinationName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Destination already exists.");
            } else {
                destinationManager.createNewDestination(mapName, destinationName, p.getLocation());
                p.sendMessage(NavigatorPlugin.getPrefix() + "§aDestination successfully set.");
                if (NavigatorPlugin.getNavConfig().auto_reload) {
                    reloadNavigationManager(p, args);
                }
            }
        } else {
            sendUsage(p);
        }
    }


    //Shows the route to the wanted destination either as particle line or as blocks
    private void showRoute(Player p, String[] args) {
        if (args.length == 3) {
            String mapName = args[1];
            String destinationName = args[2];

            if (!destinationManager.destinationExists(mapName, destinationName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Destination doesn't exists.");
            } else {
                Vertex[] path = getPath(p, mapName, destinationName);
                if (path == null) {
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§4Sorry. But I couldn't find a path to your desired destination.");
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§4The destination §6" + destinationName + " §4has insufficient checkpoints.");
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

    //A pig brings you to your wanted Location
    private void ridePigToDestination(Player p, String[] args) {
        if (args.length == 3) {
            String mapName = args[1];
            String destinationName = args[2];

            if (!destinationManager.destinationExists(mapName, destinationName)) {
                p.sendMessage(NavigatorPlugin.getPrefix() + "§4Destination doesn't exists.");
            } else {
                Vertex[] path = getPath(p, mapName, destinationName);
                if (path == null) {
                    p.sendMessage(NavigatorPlugin.getPrefix() + "§4Sorry. But I couldn't find a path to your desired destination.");
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
