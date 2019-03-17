package de.mcharvest.saith.nav.destination;

import org.bukkit.Location;


public interface IDestinationManager {

    void createNewMap(String mapName);

    boolean mapExists(String mapName);

    void createNewDestination(String mapName, String locationName, Location loc);

    boolean destinationExists(String mapName, String destinationName);

    void createNewCheckPoint(String mapName, Location loc);

    boolean checkPointExists(String mapName, Location loc);

    void removeMap(String mapName);

    void removeDestination(String mapName, String destinationName);

    void removeCheckPoint(String mapName, Location loc);

    Location[] getCheckpoints(String mapName);

    Location[] getDestinations(String mapName);

    Location[] getAllLocations(String mapName);

    Location getDestinationLocation(String mapName, String destinationName);

    String[] getDestinationNames(String mapName);

    String[] getMapNames();
}
