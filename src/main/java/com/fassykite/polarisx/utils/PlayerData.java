package com.fassykite.polarisx.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final Map<String, Integer> violations = new HashMap<>();
    private boolean bypass = false;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Player getPlayer() {
        return org.bukkit.Bukkit.getPlayer(uuid);
    }

    public int getViolations(String checkName) {
        return violations.getOrDefault(checkName.toLowerCase(), 0);
    }

    public void addViolation(String checkName) {
        String key = checkName.toLowerCase();
        violations.put(key, violations.getOrDefault(key, 0) + 1);
    }

    public void resetViolations(String checkName) {
        violations.remove(checkName.toLowerCase());
    }

    public void resetAllViolations() {
        violations.clear();
    }

    public int getTotalViolations() {
        return violations.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isBypassing() {
        Player player = getPlayer();
        if (player != null) {
            bypass = player.hasPermission("polarisx.bypass");
        }
        return bypass;
    }

    public boolean shouldCheck() {
        return !isBypassing();
    }
}