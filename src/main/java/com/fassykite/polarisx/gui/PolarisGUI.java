package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class PolarisGUI {

    public PolarisGUI() {}

    public void openMainMenu(Player player) {
        var title = MessageUtils.miniMessage().deserialize(
                "<gradient:#4A90E2:#9013FE>PolarisX v1.0</gradient>"
        );
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtils.miniMessage().deserialize("<white>✅ Speed чек активен</white>"));
        meta.lore(Collections.singletonList(
                MessageUtils.miniMessage().deserialize("<gray>Лимит: 2.5 блока/тик")
        ));
        item.setItemMeta(meta);
        inv.setItem(13, item);

        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) {
                ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta gm = glass.getItemMeta();
                gm.displayName(MessageUtils.miniMessage().deserialize("<dark_gray>"));
                glass.setItemMeta(gm);
                inv.setItem(i, glass);
            }
        }

        player.openInventory(inv);
    }
}