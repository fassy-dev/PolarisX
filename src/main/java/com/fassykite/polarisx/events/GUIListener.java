package com.fassykite.polarisx.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("PolarisX")) {
            e.setCancelled(true);
        }
    }
}