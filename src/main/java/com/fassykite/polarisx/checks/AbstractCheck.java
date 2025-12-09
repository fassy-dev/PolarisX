package com.fassykite.polarisx.checks;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.config.ChecksConfig;
import com.fassykite.polarisx.utils.MessageUtils;
import com.fassykite.polarisx.utils.PlayerData;

import java.util.UUID;

public abstract class AbstractCheck {

    protected final CheckType checkType;
    protected boolean enabled;
    protected final String checkName;
    protected int maxViolations;

    public AbstractCheck(CheckType type) {
        this.checkType = type;
        this.checkName = type.name().toLowerCase();
        this.enabled = ChecksConfig.isCheckEnabled(checkName);
        this.maxViolations = ChecksConfig.getInt(checkName + ".max-violations", 10);
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public boolean isEnabled() {
        return enabled && ChecksConfig.getGlobalBoolean("enabled", true);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCheckName() {
        return checkName;
    }

    public int getMaxViolations() {
        return maxViolations;
    }

    public abstract void handle(PlayerData data, String reason);

    protected void alert(PlayerData data, String reason) {
        String message = String.format(
                "<red>[PolarisX] %s: %s (нарушений: %d)",
                capitalize(checkName), reason, data.getViolations(checkName)
        );
        MessageUtils.sendMessage(data.getPlayer(), message);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}