package com.fassykite.polarisx.checks.impl;

import com.fassykite.polarisx.checks.Check;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.config.SettingsConfig;
import com.fassykite.polarisx.utils.MessageUtils;

public class FlyCheck extends Check {

    public FlyCheck() {
        super(CheckType.FLY);
        this.setEnabled(SettingsConfig.getBoolean("enabled-checks.fly", true));
    }

    @Override
    public void handleViolation(org.bukkit.entity.Player player, String reason) {
        MessageUtils.sendMessage(player, "<red>[PolarisX] Fly: " + reason + "</red>");
    }

    public void check(org.bukkit.entity.Player player, boolean hovering) {
        if (!isEnabled()) return;
        if (hovering) {
            handleViolation(player, "зависание в воздухе");
        }
    }
}