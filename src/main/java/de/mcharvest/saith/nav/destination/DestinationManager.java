package de.mcharvest.saith.nav.destination;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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

    //Creates a new Location by saving the current Player location into a YAML File.
    public void createNewDestination(String locationName, Location loc) {
        File locationFile = getDestinationFile(locationName);
        try {
            createLocationYml(locationFile, loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createNewCheckPoint(Location loc) {
        String checkpointName = generateCheckPointName(loc);
        File checkPointFile = getCheckPointFile(checkpointName);
        try {
            createLocationYml(checkPointFile, loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location[] getAllLocations() {
        Location[] checkPoints = getCheckpoints();
        Location[] destinations = getDestinations();
        ArrayList<Location> all = new ArrayList<>();
        Collections.addAll(all, destinations);
        Collections.addAll(all, checkPoints);

        return all.toArray(Location[]::new);
    }

    public Location[] getCheckpoints() {
        File checkpointDir = new File(defaultDir + "/Checkpoints/");
        return getLocations(checkpointDir);
    }

    public Location[] getDestinations() {
        File checkpointDir = new File(defaultDir + "/Destinations/");
        return getLocations(checkpointDir);
    }

    private Location[] getLocations(File checkpointDir) {
        ArrayList<Location> locations = new ArrayList<>();
        for (File checkPointFile : checkpointDir.listFiles()) {
            locations.add(getLocation(checkPointFile));
        }
        return locations.toArray(Location[]::new);
    }


    public boolean destinationExists(String destinationName) {
        File locationFile = getDestinationFile(destinationName);
        return locationFile.exists();
    }

    public boolean checkPointExists(Location loc) {
        return checkPointExists(generateCheckPointName(loc));
    }

    private boolean checkPointExists(String checkPointName) {
        File checkPointFile = getCheckPointFile(checkPointName);
        return checkPointFile.exists();
    }

    public Location getDestinationLocation(String destinationName) {
        File destinationFile = getDestinationFile(destinationName);
        return getLocation(destinationFile);
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

    private File getDestinationFile(String destinationName) {
        return new File(defaultDir + "/Destinations/" + destinationName + ".yml");
    }

    private File getCheckPointFile(String checkPointName) {
        return new File(defaultDir + "/Checkpoints/" + checkPointName + ".yml");
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
}
