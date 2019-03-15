package de.mcharvest.saith.nav;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//This class is for adding and removing Destinations
public class DestinationManager {
    private final File defaultDir;

    public DestinationManager(String defaultDirPath) {
        this.defaultDir = new File(defaultDirPath);
        createDefaultDirectory();
    }

    //Creates a new Location by saving the current Player location into a YAML File.
    public void createNewDestination(String locationName, Location loc) throws IOException {
        File locationFile = getDestinationFile(locationName);
        createLocationYml(locationFile, loc);
    }


    public boolean destinationExists(String destinationName) {
        File locationFile = getDestinationFile(destinationName);
        return locationFile.exists();
    }

    public void createNewCheckPoint(Location loc) throws IOException {
        String checkpointName = UUID.randomUUID().toString();
        File checkPointFile = getCheckPointFile(checkpointName);
        createLocationYml(checkPointFile, loc);
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

    private File getDestinationFile(String destinationName) {
        return new File(defaultDir + "/Destinations/" + destinationName + ".yml");
    }

    private File getCheckPointFile(String checkPointName) {
        return new File(defaultDir + "/Checkpoints/" + checkPointName + ".yml");
    }

    public Location getDestinationLocation(String destinationName) {
        File destinationFile = getDestinationFile(destinationName);
        return getLocation(destinationFile);
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
