package com.fassykite.polarisx.config;

import com.fassykite.polarisx.PolarisX;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PolarisConfig {

    private static YamlConfiguration config;

    public static void load(PolarisX plugin) {
        File file = new File(plugin.getDataFolder(), "polaris.yml");
        if (!file.exists()) {
            plugin.saveResource("polaris.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static String getString(String path, String def) {
        return config.getString(path, def);
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

    // GUI цвета
    public static String getTitleColor() {
        return getString("gui.title-color", "#4A90E2");
    }

    public static String getSecondaryColor() {
        return getString("gui.secondary-color", "#9013FE");
    }

    public static boolean useGradients() {
        return getBoolean("gui.use-gradients", true);
    }

    public static boolean useSounds() {
        return getBoolean("gui.use-sounds", true);
    }

    // Система
    public static boolean isDebug() {
        return getBoolean("system.debug", false);
    }

    public static boolean logViolations() {
        return getBoolean("system.log-violations", true);
    }

    public static boolean consoleAlerts() {
        return getBoolean("system.console-alerts", true);
    }

    // Уведомления
    public static boolean alertsEnabled() {
        return getBoolean("alerts.enabled", true);
    }

    public static boolean actionBarAlerts() {
        return getBoolean("alerts.actionbar", false);
    }

    // Наказания
    public static boolean punishmentsEnabled() {
        return getBoolean("punishments.enabled", true);
    }

    public static int getWarnThreshold() {
        return getInt("punishments.warn-threshold", 5);
    }

    public static int getKickThreshold() {
        return getInt("punishments.kick-threshold", 10);
    }

    public static int getBanThreshold() {
        return getInt("punishments.ban-threshold", 15);
    }

    // Интеграции
    public static boolean usePlaceholderAPI() {
        return getBoolean("integrations.placeholderapi", true);
    }

    public static boolean useProtocolLib() {
        return getBoolean("integrations.protocollib", true);
    }
}