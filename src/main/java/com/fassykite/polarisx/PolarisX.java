package com.fassykite.polarisx;

import com.fassykite.polarisx.checks.PolarisManager;
import com.fassykite.polarisx.events.GUIListener;
import com.fassykite.polarisx.events.PlayerMoveListener;
import com.fassykite.polarisx.gui.PolarisGUI;
import org.bukkit.plugin.java.JavaPlugin;

public final class PolarisX extends JavaPlugin {
    private static PolarisX instance;
    private PolarisManager manager;

    @Override
    public void onLoad() { instance = this; }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.manager = new PolarisManager();

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getCommand("polaris").setExecutor((s, c, l, a) -> {
            if (!s.hasPermission("polarisx.menu")) {
                s.sendMessage(com.fassykite.polarisx.utils.MessageUtils.noPermission());
                return true;
            }
            if (s instanceof org.bukkit.entity.Player p) {
                new PolarisGUI().open(p);
            }
            return true;
        });
    }

    public static PolarisX getInstance() { return instance; }
    public PolarisManager getManager() { return manager; }
}