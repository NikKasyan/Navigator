package de.mcharvest.saith;

import de.mcharvest.saith.commands.DestinationCommand;
import de.mcharvest.saith.nav.DestinationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private final DestinationManager destinationManager = new DestinationManager("plugins/Navigator/");
    private String prefix = "[Navigator]";

    @Override
    public void onEnable() {
        INSTANCE = this;
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

    public DestinationManager getDestinationManager() {
        return destinationManager;
    }
}
