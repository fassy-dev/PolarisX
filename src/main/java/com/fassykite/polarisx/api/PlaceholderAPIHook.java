package com.fassykite.polarisx.api;

import com.fassykite.polarisx.PolarisX;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "polarisx";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Fassykite";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("version")) {
            return PolarisX.getInstance().getDescription().getVersion();
        }
        return "";
    }
}