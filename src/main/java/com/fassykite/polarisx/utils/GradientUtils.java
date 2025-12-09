package com.fassykite.polarisx.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GradientUtils {

    public static String createGradient(String text, String startColor, String endColor) {
        return "<gradient:" + startColor + ":" + endColor + ">" + text + "</gradient>";
    }

    public static String createThreeColorGradient(String text, String start, String mid, String end) {
        return "<gradient:" + start + ":" + mid + ":" + end + ">" + text + "</gradient>";
    }

    public static TextColor hexToTextColor(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.length() != 6) throw new IllegalArgumentException("Invalid hex color: " + hex);
        int color = Integer.parseInt(hex, 16);
        return TextColor.color(color);
    }
}