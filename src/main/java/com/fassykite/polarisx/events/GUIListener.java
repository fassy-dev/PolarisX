package com.fassykite.polarisx.events;

import com.fassykite.polarisx.gui.ChecksGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private final ChecksGUI checksGUI = new ChecksGUI();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Проверки")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name == null) return;

            String checkKey = getCheckKey(name);
            if (checkKey == null) {
                if (name.contains("Назад")) {
                    new com.fassykite.polarisx.gui.PolarisGUI().openMainMenu((org.bukkit.entity.Player) e.getWhoClicked());
                }
                return;
            }

            if (e.isLeftClick()) {
                checksGUI.handleLeftClick((org.bukkit.entity.Player) e.getWhoClicked(), checkKey);
            } else if (e.isRightClick()) {
                checksGUI.handleRightClick((org.bukkit.entity.Player) e.getWhoClicked(), checkKey);
            }
        }
    }

    private String getCheckKey(String displayName) {
        if (displayName.contains("Speed")) return "speed";
        if (displayName.contains("Fly")) return "fly";
        if (displayName.contains("KillAura")) return "killaura";
        if (displayName.contains("Reach")) return "reach";
        if (displayName.contains("NoFall")) return "nofall";
        return null;
    }
}