package com.fassykite.polarisx.checks.impl;

import com.fassykite.polarisx.checks.Check;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.config.SettingsConfig;
import com.fassykite.polarisx.utils.MessageUtils;

public class NoFallCheck extends Check {

    public NoFallCheck() {
        super(CheckType.NOFALL);
        this.setEnabled(SettingsConfig.getBoolean("enabled-checks.nofall", true));
    }

    @Override
    public void handleViolation(org.bukkit.entity.Player player, String reason) {
        MessageUtils.sendMessage(player, "<red>[PolarisX] NoFall: " + reason + "</red>");
    }

    public void check(org.bukkit.entity.Player player, boolean damageExpected) {
        if (!isEnabled()) return;
        if (damageExpected) {
            handleViolation(player, "отсутствие урона от падения");
        }
    }
}