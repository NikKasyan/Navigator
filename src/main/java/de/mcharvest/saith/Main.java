package de.mcharvest.saith;

import de.mcharvest.saith.commands.DestinationCommand;
import de.mcharvest.saith.nav.Navigator;
import de.mcharvest.saith.nav.destination.DestinationManager;
import de.mcharvest.saith.nav.destination.IDestinationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private final IDestinationManager destinationManager = new DestinationManager("plugins/Navigator/");
    private Navigator navigator;
    private String prefix = "[Navigator]";

    @Override
    public void onEnable() {
        INSTANCE = this;
        navigator = new Navigator(destinationManager.getAllLocations());
        addDefaultsToConfig();
        registerCommands();
    }

    public void addDefaultsToConfig() {
        getConfig().addDefault("prefix", "[Navigator]");
        //getConfig().addDefault("messages.usage","/<command> [set|list]");
    }

    private void registerCommands() {
        getCommand("location").setExecutor(new DestinationCommand());
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public IDestinationManager getDestinationManager() {
        return destinationManager;
    }
    public Navigator getNavigator(){
        return navigator;
    }
}
