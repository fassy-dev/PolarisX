package com.fassykite.polarisx;

import com.fassykite.polarisx.checks.Check;
import com.fassykite.polarisx.checks.impl.*;

import java.util.HashMap;
import java.util.Map;

public class PolarisManager {

    private final Map<String, Check> checks = new HashMap<>();

    public PolarisManager() {
        registerCheck(new SpeedCheck());
        registerCheck(new FlyCheck());
        registerCheck(new KillAuraCheck());
        registerCheck(new ReachCheck());
        registerCheck(new NoFallCheck());
    }

    public void registerCheck(Check check) {
        checks.put(check.getType().name().toLowerCase(), check);
    }

    public Check getCheck(String name) {
        return checks.get(name.toLowerCase());
    }

    public Map<String, Check> getChecks() {
        return checks;
    }

    public int getActiveChecks() {
        return (int) checks.values().stream().filter(Check::isEnabled).count();
    }

    public int getGlobalViolations() {
        return 0;
    }

    public int getTotalViolations(java.util.UUID uuid) {
        return 0;
    }

    public int getOnlinePlayersWithData() {
        return PolarisX.getInstance().getServer().getOnlinePlayers().size();
    }
}