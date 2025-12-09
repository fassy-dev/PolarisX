package com.fassykite.polarisx.checks.impl;

import com.fassykite.polarisx.checks.Check;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.config.SettingsConfig;
import com.fassykite.polarisx.utils.MessageUtils;

public class KillAuraCheck extends Check {

    public KillAuraCheck() {
        super(CheckType.KILLAURA);
        this.setEnabled(SettingsConfig.getBoolean("enabled-checks.killaura", true));
    }

    @Override
    public void handleViolation(org.bukkit.entity.Player player, String reason) {
        MessageUtils.sendMessage(player, "<red>[PolarisX] KillAura: " + reason + "</red>");
    }

    public void check(org.bukkit.entity.Player player, int cps) {
        if (!isEnabled()) return;
        if (cps > 20) {
            handleViolation(player, "CPS: " + cps);
        }
    }
}