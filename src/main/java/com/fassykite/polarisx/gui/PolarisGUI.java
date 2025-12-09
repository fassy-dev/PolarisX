package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PolarisGUI {
    public void open(Player player) {
        Component title = MessageUtils.mini().deserialize("<gradient:#8B0000:#FF0000:#000000>PolarisX v1.0</gradient>");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtils.mini().deserialize("<white>✅ Speed чек активен</white>"));
        meta.lore(java.util.Collections.singletonList(
                MessageUtils.mini().deserialize("<gray>Лимит: 2.5 блока/тик")
        ));
        item.setItemMeta(meta);
        inv.setItem(13, item);

        // Заполняем стеклом
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) {
                ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta gm = glass.getItemMeta();
                gm.displayName(MessageUtils.mini().deserialize("<dark_gray>"));
                glass.setItemMeta(gm);
                inv.setItem(i, glass);
            }
        }

        player.openInventory(inv);
    }
}