package de.mcharvest.saith.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class NavigatorConfig {
    public String prefix;
    public boolean auto_reload;
    public double max_distance_between_points;

    public NavigatorConfig(FileConfiguration cfg) {
        try {
            mapYamlToObject(cfg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Config misses node.");
        }
    }

    private void mapYamlToObject(FileConfiguration cfg) throws IllegalAccessException {
        Class<? extends NavigatorConfig> clazz =  this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object obj = cfg.get(field.getName(), null);
            if(obj instanceof String){
                obj = ChatColor.translateAlternateColorCodes('&',obj.toString());
            }
            field.set(this, obj);
        }
    }
}
