package de.mcharvest.saith.nav;

import de.mcharvest.saith.Main;
import de.mcharvest.saith.nav.dijkstra.Dijkstra;
import de.mcharvest.saith.nav.dijkstra.Edge;
import de.mcharvest.saith.nav.dijkstra.Vertex;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

//Wrapper for Dijkstra Algorithm
public class Navigator {

    private Vertex[] checkpoints;
    private boolean[][] adjacencyMatrix;
    private double[][] distanceMatrix;
    //Edges of all Vertices
    private List<Edge> edges;
    //Max Distance between two Vertices
    private final double MAX_DISTANCE;
    private static double MAX_DISTANCE_STATIC = 10;
    private static HashMap<Player, Integer> tasks = new HashMap<>();

    public Navigator(double maxDistanceBetweenCheckpoints, Location[] checkpoints) {
        MAX_DISTANCE_STATIC = maxDistanceBetweenCheckpoints;
        this.MAX_DISTANCE = maxDistanceBetweenCheckpoints;
        this.checkpoints = new Vertex[checkpoints.length];
        for (int i = 0; i < checkpoints.length; i++) {
            this.checkpoints[i] = new Vertex(checkpoints[i]);
        }
        this.adjacencyMatrix = generateAdjacencyMatrix(this.checkpoints, maxDistanceBetweenCheckpoints);
        this.distanceMatrix = generateDistanceMatrix();
        this.edges = getEdgesFromAdjacentsMatrix();
    }

    public static void showPathToTravel(Player p,
                                        Vertex[] path,
                                        BiConsumer<ArrayList<Vertex>, boolean[][]> duringTravel,
                                        Runnable arrivedAtDestination) {
        final boolean[][] adjacencyMatrix = Navigator.generateAdjacencyMatrix(path, MAX_DISTANCE_STATIC);
        int task;
        if (tasks.get(p) != null) {
            Bukkit.getScheduler().cancelTask(tasks.get(p));
        }
        final ArrayList<Vertex> vertices = new ArrayList<>();
        Collections.addAll(vertices, path);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            try {
                int closest = Navigator.getClosestCheckpointIndex(p.getLocation(), vertices);
                if (vertices.get(closest).getLocation().distance(p.getLocation()) <= 2) {
                    vertices.remove(vertices.get(closest));
                }
                duringTravel.accept(vertices, adjacencyMatrix);

            } catch (IndexOutOfBoundsException e) {
                arrivedAtDestination.run();
                Bukkit.getScheduler().cancelTask(tasks.get(p));
            }

        }, 0, 10);
        tasks.put(p, task);
    }


    public Vertex[] findShortestPath(Location start, Location destination) {
        int closestCheckpointIndex = getClosestCheckPointIndex(start);
        return findShortestRoute(closestCheckpointIndex, destination);
    }


    /*
     * Finds the shortest Route from one Vertex to another
     * using the Dijkstra Algorithm
     * */
    private Vertex[] findShortestRoute(int closestCheckpointIndex, Location destination) {
        Vertex start = checkpoints[closestCheckpointIndex];
        List<Edge> edges = new ArrayList<>(this.edges);
        Dijkstra dijkstra = new Dijkstra(edges);
        Vertex[] path = dijkstra.getPathAsVertices(new Vertex(destination));
        if (path == null) {
            dijkstra.execute(start);
            return dijkstra.getPathAsVertices(new Vertex(destination));
        }
        return path;
    }


    //Generates the Adjacency matrix which indicates which Location are connected
    //Goes through every checkpoint and if the distance between two are <= MAX_DISTANCE
    //these locations are connected
    public static boolean[][] generateAdjacencyMatrix(Vertex[] checkpoints, double maxDistanceBetweenBlocks) {
        boolean[][] adjacencyMatrix = new boolean[checkpoints.length][checkpoints.length];
        for (int i = 0; i < checkpoints.length; i++) {
            for (int j = i + 1; j < checkpoints.length; j++) {
                Location loc1 = checkpoints[i].getLocation();
                Location loc2 = checkpoints[j].getLocation();
                double distanceBetweenblocks = loc1.distance(loc2);
                if (distanceBetweenblocks <= maxDistanceBetweenBlocks) {
                    adjacencyMatrix[j][i] = adjacencyMatrix[i][j] = true;
                }
            }
        }
        return adjacencyMatrix;
    }

    private double[][] generateDistanceMatrix() {
        double[][] distanceMatrix = new double[adjacencyMatrix.length][adjacencyMatrix.length];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = i + 1; j < adjacencyMatrix.length; j++) {
                Location loc1 = checkpoints[i].getLocation();
                Location loc2 = checkpoints[j].getLocation();
                double distanceBetweenblocks = loc1.distance(loc2);
                if (distanceBetweenblocks <= MAX_DISTANCE) {
                    distanceMatrix[j][i] = distanceMatrix[i][j] = distanceBetweenblocks;
                } else {
                    distanceMatrix[j][i] = distanceMatrix[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        return distanceMatrix;
    }

    public void showGraphToPlayer(Player p) {
        PathVisualizer.showGraphToPlayer(p, checkpoints);
    }


    private List<Edge> getEdgesFromAdjacentsMatrix() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < checkpoints.length; i++)
            for (int j = i + 1; j < checkpoints.length; j++) {
                if (adjacencyMatrix[i][j]) {
                    edges.add(new Edge(checkpoints[i], checkpoints[j], distanceMatrix[i][j]));
                    edges.add(new Edge(checkpoints[j], checkpoints[i], distanceMatrix[i][j]));
                }
            }
        return edges;
    }

    private int getClosestCheckPointIndex(Location loc) {
        int closestLocationIndex = -1;
        double smallestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < checkpoints.length; i++) {
            Location checkpoint = checkpoints[i].getLocation();
            double currentDistance = loc.distance(checkpoint);
            if (currentDistance < smallestDistance) {
                smallestDistance = currentDistance;
                closestLocationIndex = i;
            }
        }
        return closestLocationIndex;
    }

    public static int getClosestCheckpointIndex(Location loc, ArrayList<Vertex> checkpoints) {
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
}
