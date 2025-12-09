package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.config.MessagesConfig;
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
        String title = MessagesConfig.getRaw("gui-title-player");
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 27,
                MessageUtils.miniMessage().deserialize(title));

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(target);
            meta.displayName(MessageUtils.miniMessage().deserialize(
                    "<gradient:#4A90E2:#9013FE>" + target.getName() + "</gradient>"));
            meta.lore(Collections.singletonList("<gray>UUID: " + target.getUniqueId()));
            head.setItemMeta(meta);
        }
        inv.setItem(13, head);

        inv.setItem(22, createButton(Material.ARROW, "<gray>← Назад", null));

        fillWithGlass(inv);
        viewer.openInventory(inv);
    }

    private ItemStack createButton(Material mat, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtils.miniMessage().deserialize(name));
            if (lore != null) {
                meta.lore(lore.stream().map(l -> MessageUtils.miniMessage().deserialize(l)).toList());
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void fillWithGlass(Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = glass.getItemMeta();
                if (meta != null) {
                    meta.displayName(MessageUtils.miniMessage().deserialize("<dark_gray>"));
                    glass.setItemMeta(meta);
                }
                inv.setItem(i, glass);
            }
        }
    }
}