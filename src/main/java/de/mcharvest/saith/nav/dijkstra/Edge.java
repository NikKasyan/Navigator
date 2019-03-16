package de.mcharvest.saith.nav.dijkstra;

//Represents the "line" between two vertices
public class Edge {
    private final Vertex source;
    private final Vertex destination;
    private final double distance;

    public Edge(Vertex source, Vertex destination, double distance) {
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }
    public boolean containsVertex(Vertex vertex){
        return source.equals(vertex) || destination.equals(vertex);
    }
    public double getDistance() {
        return distance;
    }
    public String toString(){
        return source+" <-> "+destination;
    }
}
