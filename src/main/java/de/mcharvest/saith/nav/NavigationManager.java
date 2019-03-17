package de.mcharvest.saith.nav;

import de.mcharvest.saith.NavigatorPlugin;
import de.mcharvest.saith.nav.destination.IDestinationManager;

import java.util.HashMap;

public class NavigationManager {
    private HashMap<String, Navigator> navigatorsByMap = new HashMap<>();
    private IDestinationManager destinationManager;
    public NavigationManager(IDestinationManager destinationManager) {
        this.destinationManager = destinationManager;
        reload();
    }
    public void reload(String mapName){
        double maxDistance = NavigatorPlugin.getNavConfig().max_distance_between_points;
        if (destinationManager.getAllLocations(mapName) != null)
            navigatorsByMap.put(mapName, new Navigator(maxDistance, destinationManager.getAllLocations(mapName)));
    }
    public void reload(){

        for (String mapName : destinationManager.getMapNames()) {
            reload(mapName);
        }
    }
    public Navigator getNavigator(String mapName) {
        return navigatorsByMap.get(mapName);
    }
}
