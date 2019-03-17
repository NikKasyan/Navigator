package de.mcharvest.saith.nav;

import de.mcharvest.saith.NavigatorPlugin;
import de.mcharvest.saith.nav.destination.IDestinationManager;

import java.util.HashMap;

public class NavigationManager {
    private HashMap<String, Navigator> navigatorsByMap = new HashMap<>();

    public NavigationManager(IDestinationManager destinationManager) {
        double maxDistance = NavigatorPlugin.getInstance().getConfig().getDouble("max_distance_between_points");
        for (String mapName : destinationManager.getMapNames()) {
            if (destinationManager.getAllLocations(mapName) != null)
                navigatorsByMap.put(mapName, new Navigator(maxDistance, destinationManager.getAllLocations(mapName)));
        }
    }

    public Navigator getNavigator(String mapName) {
        return navigatorsByMap.get(mapName);
    }
}
