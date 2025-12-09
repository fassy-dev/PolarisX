package com.fassykite.polarisx.config;

import com.fassykite.polarisx.PolarisX;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SettingsConfig {

    private static YamlConfiguration config;

    public static void load(PolarisX plugin) {
        File file = new File(plugin.getDataFolder(), "settings.yml");
        if (!file.exists()) {
            plugin.saveResource("settings.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public static double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public static int getInt(String path, int def) {
        return config.getInt(path, def);
    }
}