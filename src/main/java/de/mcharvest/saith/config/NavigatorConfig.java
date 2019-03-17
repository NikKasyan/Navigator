package de.mcharvest.saith.config;

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
        Class<NavigatorConfig> clazz = (Class<NavigatorConfig>) this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(this, cfg.get(field.getName(), null));
        }
    }
}
