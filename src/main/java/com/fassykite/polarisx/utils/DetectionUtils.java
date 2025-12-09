package com.fassykite.polarisx.utils;

public class DetectionUtils {

    public static boolean isBypassing(org.bukkit.entity.Player player) {
        return player.hasPermission("polarisx.bypass");
    }

    public static double distance3D(org.bukkit.Location a, org.bukkit.Location b) {
        return Math.sqrt(
                Math.pow(a.getX() - b.getX(), 2) +
                        Math.pow(a.getY() - b.getY(), 2) +
                        Math.pow(a.getZ() - b.getZ(), 2)
        );
    }
}