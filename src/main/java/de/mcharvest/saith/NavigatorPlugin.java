package de.mcharvest.saith;

import de.mcharvest.saith.commands.DestinationCommand;
import de.mcharvest.saith.listeners.CheckPointListener;
import de.mcharvest.saith.nav.NavigationManager;
import de.mcharvest.saith.nav.destination.DestinationManager;
import de.mcharvest.saith.nav.destination.IDestinationManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class NavigatorPlugin extends JavaPlugin {

    private static NavigatorPlugin INSTANCE;
    private final IDestinationManager destinationManager = new DestinationManager("plugins/Navigator/");
    private NavigationManager navigationManager;
    private String prefix = "[Navigator]";

    @Override
    public void onEnable() {
        INSTANCE = this;
        addDefaultsToConfig();
        prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("prefix",prefix));
        navigationManager = new NavigationManager(destinationManager);
        registerCommands();
        registerListeners();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CheckPointListener(), this);
    }

    private void addDefaultsToConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        //getConfig().addDefault("messages.usage","/<command> [set|list]");
    }

    private void registerCommands() {
        getCommand("destination").setExecutor(new DestinationCommand());
    }

    public static NavigatorPlugin getInstance() {
        return INSTANCE;
    }

    public static String getPrefix(){
        return getInstance().prefix;
    }
    public IDestinationManager getDestinationManager() {
        return destinationManager;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}
