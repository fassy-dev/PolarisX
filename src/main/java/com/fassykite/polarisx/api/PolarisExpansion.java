package com.fassykite.polarisx.api;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.config.PolarisConfig;
import com.fassykite.polarisx.utils.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PolarisExpansion extends PlaceholderExpansion {

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
        return PolarisX.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        UUID uuid = player.getUniqueId();
        PlayerData data = PolarisX.getInstance().getPlayerData(uuid);

        switch (params.toLowerCase()) {
            case "version":
                return getVersion();
            case "violations":
                return String.valueOf(data.getTotalViolations());
            case "speed_violations":
                return String.valueOf(data.getViolations("speed"));
            case "fly_violations":
                return String.valueOf(data.getViolations("fly"));
            case "killaura_violations":
                return String.valueOf(data.getViolations("killaura"));
            case "reach_violations":
                return String.valueOf(data.getViolations("reach"));
            case "nofall_violations":
                return String.valueOf(data.getViolations("nofall"));
            case "status":
                int total = data.getTotalViolations();
                if (total == 0) return "CLEAN";
                if (total < PolarisConfig.getWarnThreshold()) return "SUSPICIOUS";
                if (total < PolarisConfig.getKickThreshold()) return "HIGH_RISK";
                return "DANGEROUS";
            case "enabled":
                return String.valueOf(PolarisConfig.getBoolean("system.enabled", true));
            default:
                return "";
        }
    }
}