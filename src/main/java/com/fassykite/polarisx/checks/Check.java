package com.fassykite.polarisx.checks;

public abstract class Check {
    private final CheckType type;
    protected boolean enabled = true;

    public Check(CheckType type) { this.type = type; }

    public CheckType getType() { return type; }
    public boolean isEnabled() { return enabled; }
    public abstract void handleViolation(org.bukkit.entity.Player p, String r);
}