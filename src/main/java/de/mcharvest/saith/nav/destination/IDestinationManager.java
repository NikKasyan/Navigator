package de.mcharvest.saith.nav.destination;

import org.bukkit.Location;


public interface IDestinationManager {

    void createNewDestination(String locationName, Location loc);
    boolean destinationExists(String destinationName);
    void createNewCheckPoint(Location loc);
    boolean checkPointExists(Location loc);
    Location[] getCheckpoints();
    Location[] getDestinations();
    Location[] getAllLocations();
    Location getDestinationLocation(String destinationName);
}
