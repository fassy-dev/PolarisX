package com.fassykite.polarisx.checks.impl;

import com.fassykite.polarisx.checks.Check;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.config.SettingsConfig;
import com.fassykite.polarisx.utils.MessageUtils;

public class ReachCheck extends Check {

    private final double maxDistance;

    public ReachCheck() {
        super(CheckType.REACH);
        this.maxDistance = SettingsConfig.getDouble("check.reach.max-distance", 3.2);
        this.setEnabled(SettingsConfig.getBoolean("enabled-checks.reach", true));
    }

    @Override
    public void handleViolation(org.bukkit.entity.Player player, String reason) {
        MessageUtils.sendMessage(player, "<red>[PolarisX] Reach: " + reason + "</red>");
    }

    public void check(org.bukkit.entity.Player player, double distance) {
        if (!isEnabled()) return;
        if (distance > maxDistance) {
            handleViolation(player, "дистанция: " + String.format("%.2f", distance));
        }
    }
}