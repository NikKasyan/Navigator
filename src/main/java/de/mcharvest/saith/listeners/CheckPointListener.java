package de.mcharvest.saith.listeners;

import de.mcharvest.saith.NavigatorPlugin;
import de.mcharvest.saith.nav.destination.IDestinationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;

//This is the Listener for the EditCheckPointMode
//After the Player quits the EditCheckPointMode all placed Blocks are removed
public class CheckPointListener implements Listener {

    private IDestinationManager destinationManager = NavigatorPlugin.getInstance().getDestinationManager();
    private static HashMap<Player, String> playersEditCheckpointMode = new HashMap<>();
    //Remembers the placedBlocks
    private static HashMap<Player, ArrayList<Block>> placedBlocks = new HashMap<>();

    private Block block1;
    private Block block2;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (playersEditCheckpointMode.containsKey(p)) {
            if (event.getBlock().getType() == Material.GOLD_BLOCK) {
                addCheckpoint(p, event.getBlock().getLocation());
                ArrayList<Block> blocks = placedBlocks.get(p);
                blocks.add(event.getBlock());

            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (playersEditCheckpointMode.containsKey(p)) {
            if (event.getBlock().getType() == Material.GOLD_BLOCK) {
                removeCheckPoint(p, event.getBlock().getLocation());
                ArrayList<Block> blocks = placedBlocks.get(p);
                if (blocks != null) {
                    blocks.remove(event.getBlock());
                }

            }
        }
    }

    public static void enableCheckpointEditMode(Player p, String mapName) {
        playersEditCheckpointMode.put(p, mapName);
        placedBlocks.put(p, new ArrayList<>());
    }

    //Removes all placed Goldblocks
    public static void disableCheckpointEditMode(Player p) {
        playersEditCheckpointMode.remove(p);

        for (Block block : placedBlocks.get(p)) {
            block.setType(Material.AIR);
        }
        placedBlocks.remove(p);
    }

    public static boolean isInCheckpointEditMode(Player p) {
        return playersEditCheckpointMode.containsKey(p);
    }

    private void addCheckpoint(Player p, Location loc) {
        String mapName = playersEditCheckpointMode.get(p);
        if (destinationManager.checkPointExists(mapName, loc)) {
            p.sendMessage(NavigatorPlugin.getPrefix() + "§4Checkpoint already exists.");
        } else {
            destinationManager.createNewCheckPoint(mapName, loc);
            p.sendMessage(NavigatorPlugin.getPrefix() + "§aCheckpoint successfully set.");
        }
    }

    private void removeCheckPoint(Player p, Location loc) {
        String mapName = playersEditCheckpointMode.get(p);
        if (!destinationManager.checkPointExists(mapName, loc)) {
            return;
        }
        destinationManager.removeCheckPoint(mapName, loc);
        p.sendMessage(NavigatorPlugin.getPrefix() + "§aCheckpoint successfully removed.");
    }
}
