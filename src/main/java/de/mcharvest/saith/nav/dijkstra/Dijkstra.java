package de.mcharvest.saith.nav.dijkstra;


import org.bukkit.Location;

import java.util.*;

public class Dijkstra {


    private final List<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unsettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Double> distances;

    public Dijkstra(List<Edge> edges) {
        this.edges = new ArrayList<>(edges);
        predecessors = new HashMap<>();
    }


    public void execute(Vertex source) {
        settledNodes = new HashSet<>();
        unsettledNodes = new HashSet<>();
        distances = new HashMap<>();
        distances.put(source, 0.0);
        unsettledNodes.add(source);
        while (unsettledNodes.size() > 0) {
            Vertex node = getMinimum(unsettledNodes);
            settledNodes.add(node);
            unsettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    //Gets the Vertex with the shortest distance(from the source Vertex)
    private Vertex getMinimum(Set<Vertex> vertices) {
        Vertex minimum = null;
        for (Vertex vertex : vertices) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    //Get the shortest Distance to the given Vertex
    private double getShortestDistance(Vertex destination) {
        return distances.getOrDefault(destination, Double.POSITIVE_INFINITY);
    }

    //Finds the smallest distance from the vertex
    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbours(node);
        for (Vertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distances.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unsettledNodes.add(target);
            }
        }
    }

    //Gets all Vertices next to the node if the vertex is not settled
    private List<Vertex> getNeighbours(Vertex node) {
        List<Vertex> neighbours = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
                neighbours.add(edge.getDestination());
            }
        }
        return neighbours;
    }

    //Gets the distance/weight of two vertices/one edge
    private double getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
                return edge.getDistance();
            }
        }
        throw new RuntimeException("");
    }


    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }


    public LinkedList<Location> getPath(Vertex target) {
        LinkedList<Vertex> path = getPathAsVertexList(target);
        if (path == null)
            return null;
        LinkedList<Location> pathLocations = new LinkedList<>();
        for (Vertex vertex : path)
            pathLocations.add(vertex.getLocation());
        return pathLocations;
    }

    public LinkedList<Vertex> getPathAsVertexList(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<>();
        Vertex step = target;
        if (getPredecessor(step) == null) {
            return null;
        }
        path.add(step);
        while (getPredecessor(step) != null) {
            step = getPredecessor(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    public Vertex[] getPathAsVertices(Vertex target) {
        LinkedList<Vertex> vertices = getPathAsVertexList(target);
        if (vertices == null)
            return null;
        return vertices.toArray(Vertex[]::new);
    }

    private Vertex getPredecessor(Vertex vertex) {
        for (Vertex v : predecessors.keySet()) {
            if (v.equals(vertex)) {
                return predecessors.get(v);
            }
        }
        return null;
    }
}
