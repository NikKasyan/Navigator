package de.mcharvest.saith.nav;

import de.mcharvest.saith.NavigatorPlugin;
import de.mcharvest.saith.nav.dijkstra.Vertex;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class PathVisualizer {
    private static HashMap<Player, Integer> tasks = new HashMap<>();

    public static void showGraphToPlayer(Player p, Vertex[] path) {
        Navigator.showPathToTravel(p, path, (vertices, adjacencyMatrix) -> {
            int closest = Navigator.getClosestCheckpointIndex(p.getLocation(), vertices);
            removeClosestVertexInRange(p.getLocation(),vertices,3);
            drawLineForPlayer(p, vertices.get(closest).getLocation(), p.getLocation(), 0.1);
            drawPathToVertices(vertices, p, adjacencyMatrix);
            sendDistanceInfo(p, vertices);
        }, () -> {
            arrivedAtDestination(p);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
        });
    }

    private static void sendDistanceInfo(Player p, ArrayList<Vertex> vertices) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("§aDistance %.2fm", getDistance(p, vertices))));

    }

    private static double getDistance(Player p, ArrayList<Vertex> vertices) {
        double sum = 0;
        for (int i = 1; i < vertices.size() - 1; i++) {
            sum += vertices.get(i).getLocation().distance(vertices.get(i + 1).getLocation());
        }
        sum += p.getLocation().distance(vertices.get(0).getLocation());
        return sum;
    }

    private static void arrivedAtDestination(Player p) {
        p.sendMessage(NavigatorPlugin.getPrefix()+"§aYou have arrived at your destination.");
    }

    //Draws the path to the different Vertices
    private static void drawPathToVertices(ArrayList<Vertex> vertices, Player p, boolean[][] adjacencyMatrix) {
        for (int i = 0; i < vertices.size(); i++)
            for (int j = i + 1; j < vertices.size(); j++) {
                if (adjacencyMatrix[i][j]) {
                    if (p.isOnline()) {
                        if (p.getLocation().distance(vertices.get(j).getLocation()) <= 3) {
                            adjacencyMatrix[i][j] = adjacencyMatrix[j][i] = false;
                        }
                        drawLineForPlayer(p, vertices.get(i).getLocation(), vertices.get(j).getLocation(), 0.1);
                    } else {
                        Bukkit.getScheduler().cancelTask(tasks.get(p));
                    }
                }
            }
    }


    public static void drawLineForPlayer(Player p, Location point1, Location point2, double space) {
        point1 = point1.getBlock().getLocation().add(0.5, 0, 0.5);
        point2 = point2.getBlock().getLocation().add(0.5, 0, 0.5);
        World world = point1.getWorld();
        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        double length = 0;
        for (; length < distance; p1.add(vector)) {
            Particle.DustOptions options = new Particle.DustOptions(Color.BLUE, 0.5f);
            p.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY() + 1, p1.getZ(), 1, options);
            length += space;
        }
    }

    public static void showBlocksToPlayer(Player p, Vertex[] path) {

        Navigator.showPathToTravel(p, path, (vertices, adjacencyMatrix) -> {
            removeClosestVertexInRange(p.getLocation(),vertices,3);
            for(Vertex vertex:path){
                p.sendBlockChange(vertex.getLocation(),vertex.getLocation().getBlock().getBlockData());
            }
            for(Vertex vertex:vertices){
                p.sendBlockChange(vertex.getLocation(), Material.DIAMOND_BLOCK.createBlockData());
            }
        }, () -> {
            arrivedAtDestination(p);
            for(Vertex vertex:path){
                p.sendBlockChange(vertex.getLocation(),vertex.getLocation().getBlock().getBlockData());
            }
        });
    }
    private static void removeClosestVertexInRange(Location loc, ArrayList<Vertex> vertices, int range){
        int closest = Navigator.getClosestCheckpointIndex(loc, vertices);
        if (vertices.get(closest).getLocation().distance(loc) <= range) {
            vertices.remove(vertices.get(closest));
        }
    }
}
