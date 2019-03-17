package de.mcharvest.saith.listeners;

import de.mcharvest.saith.Main;
import de.mcharvest.saith.nav.destination.IDestinationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;

public class CheckPointListener implements Listener {

    private IDestinationManager destinationManager = Main.getInstance().getDestinationManager();
    private static HashMap<Player, String> playersAddCheckpointMode = new HashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (playersAddCheckpointMode.containsKey(p)) {
            if (event.getBlock().getType() == Material.GOLD_BLOCK) {
                addCheckpoint(p, event.getBlock().getLocation());
            }else {
                p.sendMessage("§4You can't place blocks in CheckPointAddMode.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (playersAddCheckpointMode.containsKey(p)) {
            if (event.getBlock().getType() == Material.GOLD_BLOCK) {
                removeCheckPoint(p, event.getBlock().getLocation());
            }else{
                p.sendMessage("§4You can't break blocks in CheckPointAddMode.");
                event.setCancelled(true);
            }
        }
    }

    public static void enableCheckpointAddMode(Player p, String mapName) {
        playersAddCheckpointMode.put(p, mapName);
    }

    public static void disableCheckpointAddMode(Player p) {
        playersAddCheckpointMode.remove(p);
    }

    public static boolean isInCheckpointAddMode(Player p) {
        return playersAddCheckpointMode.containsKey(p);
    }

    private void addCheckpoint(Player p, Location loc) {
        String mapName = playersAddCheckpointMode.get(p);
        if (destinationManager.checkPointExists(mapName, loc)) {
            p.sendMessage("§4Checkpoint already exists.");
        } else {
            destinationManager.createNewCheckPoint(mapName, loc);
            p.sendMessage("§aCheckpoint successfully set.");
        }
    }

    private void removeCheckPoint(Player p, Location loc) {
        String mapName = playersAddCheckpointMode.get(p);
        destinationManager.removeCheckPoint(mapName, loc);
        p.sendMessage("§aCheckpoint successfully removed.");
    }
}
