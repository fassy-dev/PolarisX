package com.fassykite.polarisx.config;

import com.fassykite.polarisx.PolarisX;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesConfig {

    private static YamlConfiguration config;

    public static void load(PolarisX plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static String getRaw(String key) {
        return config.getString(key, "&cMissing: " + key);
    }
}