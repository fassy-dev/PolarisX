package com.fassykite.polarisx.config;

import com.fassykite.polarisx.PolarisX;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ChecksConfig {

    private static YamlConfiguration config;

    public static void load(PolarisX plugin) {
        File file = new File(plugin.getDataFolder(), "checks.yml");
        if (!file.exists()) {
            plugin.saveResource("checks.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean isCheckEnabled(String checkName) {
        return config.getBoolean(checkName + ".enabled", true);
    }

    public static double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public static int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public static boolean getGlobalBoolean(String path, boolean def) {
        return config.getBoolean("global." + path, def);
    }
}