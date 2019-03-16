package de.mcharvest.saith.nav.dijkstra;

import de.mcharvest.saith.nav.destination.DestinationManager;
import org.bukkit.Location;

public class Vertex {
    private final Location loc;

    public Vertex(Location loc) {
        this.loc = loc;
    }

    public Location getLocation() {
        return loc;
    }

    public boolean equals(Object object) {
       if(object instanceof  Vertex) {
           return loc.getBlock().getLocation().equals(((Vertex) object).getLocation().getBlock().getLocation());
       }
       return false;
    }

    @Override
    public String toString() {
        return locationToString(loc);
    }
    private String locationToString(Location loc) {
        String s = "";
        s += "world= " + loc.getWorld().getName();
        s += ", x= " + loc.getBlockX();
        s += ", y= " + loc.getBlockY();
        s += ", z= "  + loc.getBlockZ();
        return s;

    }
}
