package de.mcharvest.saith.nav.destination;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

//This class is for adding and removing Destinations and Checkpoints
public class DestinationManager implements IDestinationManager {
    private final File defaultDir;

    public DestinationManager(String defaultDirPath) {
        this.defaultDir = new File(defaultDirPath);
        createDefaultDirectory();
    }

    @Override
    public void createNewMap(String mapName) {
        File mapDir = new File(defaultDir + "/" + mapName);
        mapDir.mkdirs();
    }

    @Override
    public boolean mapExists(String mapName) {
        File mapDir = new File(defaultDir + "/" + mapName);
        return mapDir.exists();
    }

    //Creates a new Location by saving the current Player location into a YAML File.
    public void createNewDestination(String mapName, String locationName, Location loc) {
        File locationFile = getDestinationFile(mapName, locationName);
        try {
            createLocationYml(locationFile, loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createNewCheckPoint(String mapName, Location loc) {
        String checkpointName = generateCheckPointName(loc);
        File checkPointFile = getCheckPointFile(mapName, checkpointName);
        try {
            createLocationYml(checkPointFile, loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location[] getAllLocations(String mapName) {
        Location[] checkPoints = getCheckpoints(mapName);
        Location[] destinations = getDestinations(mapName);
        ArrayList<Location> all = new ArrayList<>();
        Collections.addAll(all, destinations);
        Collections.addAll(all, checkPoints);

        return all.toArray(Location[]::new);
    }

    public Location[] getCheckpoints(String mapName) {
        return getLocationsInDir(getCheckPointDir(mapName));
    }

    public Location[] getDestinations(String mapName) {
        File destinationDir = getDestinationDir(mapName);
        return getLocationsInDir(destinationDir);
    }

    private Location[] getLocationsInDir(File dir) {
        ArrayList<Location> locations = new ArrayList<>();
        for (File checkPointFile : dir.listFiles()) {
            if (checkPointFile != null)
                locations.add(getLocation(checkPointFile));
        }
        return locations.toArray(Location[]::new);
    }


    public boolean destinationExists(String mapName, String destinationName) {
        File locationFile = getDestinationFile(mapName, destinationName);
        return locationFile.exists();
    }

    public boolean checkPointExists(String mapName, Location loc) {
        return checkPointExists(mapName, generateCheckPointName(loc));
    }

    @Override
    public void removeMap(String mapName) {
        File mapDir = new File(defaultDir + "/" + mapName);
        mapDir.delete();
    }

    @Override
    public void removeDestination(String mapName, String destinationName) {
        getDestinationFile(mapName, destinationName).delete();
    }

    @Override
    public void removeCheckPoint(String mapName, Location loc) {
        getCheckPointFile(mapName, generateCheckPointName(loc)).delete();
    }

    private boolean checkPointExists(String mapName, String checkPointName) {
        File checkPointFile = getCheckPointFile(mapName, checkPointName);
        return checkPointFile.exists();
    }

    public Location getDestinationLocation(String mapName, String destinationName) {
        File destinationFile = getDestinationFile(mapName, destinationName);
        return getLocation(destinationFile);
    }

    @Override
    public String[] getDestinationNames(String mapName) {
        File destinationDir = getDestinationDir(mapName);
        return destinationDir.list();
    }

    @Override
    public String[] getMapNames() {
        return defaultDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                return !name.contains(".");
            }
        });
    }

    private void createLocationYml(File ymlFile, Location loc) throws IOException {
        File parentDir = ymlFile.getParentFile();
        if (!parentDir.exists())
            parentDir.mkdirs();

        ymlFile.createNewFile();
        YamlConfiguration locationConfig = YamlConfiguration.loadConfiguration(ymlFile);
        locationConfig.set("Location", loc);
        locationConfig.save(ymlFile);
    }

    private String generateCheckPointName(Location loc) {
        return UUID.nameUUIDFromBytes(locationToString(loc).getBytes()).toString();
    }

    private String locationToString(Location loc) {
        String s = "";
        s += "world=" + loc.getWorld().getName();
        s += "x=" + loc.getBlockX();
        s += "y=" + loc.getBlockY();
        s += "z=" + loc.getBlockZ();
        return s;

    }

    private File getDestinationFile(String mapName, String destinationName) {
        return new File(getDestinationDir(mapName) + "/" + destinationName + ".yml");
    }

    private File getCheckPointFile(String mapName, String checkPointName) {
        return new File(getCheckPointDir(mapName) + "/" + checkPointName + ".yml");
    }


    private Location getLocation(File f) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
        return yml.getSerializable("Location", Location.class);
    }

    //Create the Default directory if it doesn't exist
    private void createDefaultDirectory() {
        if (!defaultDir.exists())
            defaultDir.mkdirs();
    }

    private File getCheckPointDir(String mapName) {
        File checkPointDir = new File(defaultDir + "/" + mapName + "/Checkpoints/");
        if (!checkPointDir.exists())
            checkPointDir.mkdirs();
        return checkPointDir;
    }

    private File getDestinationDir(String mapName) {
        File destinationDir = new File(defaultDir + "/" + mapName + "/Destinations/");
        if (!destinationDir.exists())
            destinationDir.mkdirs();
        return destinationDir;
    }
}
