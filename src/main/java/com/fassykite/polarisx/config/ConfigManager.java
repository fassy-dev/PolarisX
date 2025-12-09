package com.fassykite.polarisx.config;

import com.fassykite.polarisx.PolarisX;
import java.io.File;

public class ConfigManager {

    public static void loadConfigs(PolarisX plugin) {
        loadFile(plugin, "config.yml");
        loadFile(plugin, "settings.yml");
        loadFile(plugin, "messages.yml");

        SettingsConfig.load(plugin);
        MessagesConfig.load(plugin);
    }

    private static void loadFile(PolarisX plugin, String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
    }
}