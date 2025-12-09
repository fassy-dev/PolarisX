package com.fassykite.polarisx.checks;

import com.fassykite.polarisx.checks.impl.SpeedCheck;

import java.util.HashMap;
import java.util.Map;

public class PolarisManager {
    private final Map<String, Check> checks = new HashMap<>();

    public PolarisManager() {
        registerCheck(new SpeedCheck());
    }

    public void registerCheck(Check check) {
        checks.put(check.getType().name().toLowerCase(), check);
    }

    public Check getCheck(String name) {
        return checks.get(name.toLowerCase());
    }
}