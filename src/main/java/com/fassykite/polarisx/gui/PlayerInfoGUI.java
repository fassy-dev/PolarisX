package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;

public class PlayerInfoGUI {

    public void open(Player viewer, Player target) {
        var title = MessageUtils.miniMessage().deserialize(
                "<gradient:#4A90E2:#9013FE>Информация об игроке</gradient>"
        );
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, title);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(target);
            meta.displayName(MessageUtils.miniMessage().deserialize(
                    "<gradient:#4A90E2:#9013FE>" + target.getName() + "</gradient>"
            ));
            meta.lore(Collections.singletonList(
                    MessageUtils.miniMessage().deserialize("<gray>UUID: " + target.getUniqueId())
            ));
            head.setItemMeta(meta);
        }
        inv.setItem(13, head);

        // Заполняем стеклом
        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null) {
                ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta gm = glass.getItemMeta();
                gm.displayName(MessageUtils.miniMessage().deserialize("<dark_gray>"));
                glass.setItemMeta(gm);
                inv.setItem(i, glass);
            }
        }

        viewer.openInventory(inv);
    }
}