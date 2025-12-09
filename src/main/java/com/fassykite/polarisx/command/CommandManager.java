package com.fassykite.polarisx.command;

import com.fassykite.polarisx.PolarisX;

public class CommandManager {

    private final PolarisX plugin;

    public CommandManager(PolarisX plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("polaris").setExecutor(new PolarisCommand());
        plugin.getCommand("polaris").setTabCompleter(new PolarisTabCompleter());
        plugin.getCommand("alerts").setExecutor(new AlertsCommand());
    }
}