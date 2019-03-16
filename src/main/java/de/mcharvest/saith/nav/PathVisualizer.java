package de.mcharvest.saith.nav;

import de.mcharvest.saith.Main;
import de.mcharvest.saith.nav.dijkstra.Vertex;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PathVisualizer {
    private static HashMap<Player, Integer> tasks = new HashMap<>();

    public static void showGraphToPlayer(Player p, Vertex[] checkpoints/*, BiConsumer<ArrayList<Vertex>,Player> function*/){
        final boolean[][] adjacencyMatrix = Navigator.generateAdjacencyMatrix(checkpoints);
        int task;
        if (tasks.get(p) != null) {
            Bukkit.getScheduler().cancelTask(tasks.get(p));
        }
        final ArrayList<Vertex> vertices = new ArrayList<>();
        Collections.addAll(vertices, checkpoints);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            try {
                int closest = getClosestCheckpointIndex(p.getLocation(), vertices);
                if (vertices.get(closest).getLocation().distance(p.getLocation()) <= 3) {
                    vertices.remove(vertices.get(closest));

                }
                drawLineForPlayer(p, vertices.get(closest).getLocation(), p.getLocation(), 0.1);
                drawPathToVertices(vertices,p,adjacencyMatrix);
            } catch (IndexOutOfBoundsException e) {
                p.sendMessage("§aYou have arrived at your destination.");
                Bukkit.getScheduler().cancelTask(tasks.get(p));
            }

        }, 0, 10);
        tasks.put(p, task);
    }

    //Draws the path to the different Vertices
    private static void drawPathToVertices(ArrayList<Vertex> vertices, Player p, boolean[][] adjacencyMatrix){
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
    private static int getClosestCheckpointIndex(Location loc, ArrayList<Vertex> checkpoints) {
        int index = 0;
        double minimumDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < checkpoints.size(); i++) {
            if (checkpoints.get(i) == null)
                continue;
            if (checkpoints.get(i).getLocation().distance(loc) < minimumDistance) {
                minimumDistance = checkpoints.get(i).getLocation().distance(loc);
                index = i;
            }
        }
        return index;
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
            p.spawnParticle(Particle.REDSTONE, p1.getX(), p.getLocation().getY() + 1, p1.getZ(), 1, options);
            length += space;
        }
    }

}
