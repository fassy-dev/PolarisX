package com.fassykite.polarisx.utils;

public class MathUtils {

    public static double distance3D(org.bukkit.Location a, org.bukkit.Location b) {
        return Math.sqrt(
                Math.pow(a.getX() - b.getX(), 2) +
                        Math.pow(a.getY() - b.getY(), 2) +
                        Math.pow(a.getZ() - b.getZ(), 2)
        );
    }

    public static double distance2D(org.bukkit.Location a, org.bukkit.Location b) {
        return Math.sqrt(
                Math.pow(a.getX() - b.getX(), 2) +
                        Math.pow(a.getZ() - b.getZ(), 2)
        );
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double average(double... values) {
        if (values.length == 0) return 0;
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }

    public static double standardDeviation(double[] values) {
        if (values.length == 0) return 0;
        double mean = average(values);
        double sum = 0;
        for (double v : values) {
            sum += Math.pow(v - mean, 2);
        }
        return Math.sqrt(sum / values.length);
    }
}