package com.fassykite.polarisx.checks.impl;

import com.fassykite.polarisx.checks.Check;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.utils.MessageUtils;

public class SpeedCheck extends Check {

    public SpeedCheck() {
        super(CheckType.SPEED);
    }

    public void check(org.bukkit.entity.Player player, double speed) {
        if (!isEnabled()) return;
        if (speed > 2.5) {
            handleViolation(player, "скорость: " + String.format("%.2f", speed));
        }
    }

    @Override
    public void handleViolation(org.bukkit.entity.Player player, String reason) {
        player.sendMessage(MessageUtils.miniMessage().deserialize(
                "<red>[PolarisX] Speed: " + reason + "</red>"
        ));
    }
}