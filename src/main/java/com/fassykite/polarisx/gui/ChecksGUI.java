package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.config.MessagesConfig;
import com.fassykite.polarisx.config.SettingsConfig;
import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Collections;

public class ChecksGUI {

    public void open(Player player) {
        String title = MessagesConfig.getRaw("gui-title-checks");
        Inventory inv = Bukkit.createInventory(null, 27,
                MessageUtils.miniMessage().deserialize(title));

        boolean speed = SettingsConfig.getBoolean("enabled-checks.speed", true);
        boolean fly = SettingsConfig.getBoolean("enabled-checks.fly", true);
        boolean killaura = SettingsConfig.getBoolean("enabled-checks.killaura", true);
        boolean reach = SettingsConfig.getBoolean("enabled-checks.reach", true);
        boolean nofall = SettingsConfig.getBoolean("enabled-checks.nofall", true);

        inv.setItem(10, createToggle(Material.FEATHER, "Speed", speed, "speed"));
        inv.setItem(11, createToggle(Material.ELYTRA, "Fly", fly, "fly"));
        inv.setItem(12, createToggle(Material.DIAMOND_SWORD, "KillAura", killaura, "killaura"));
        inv.setItem(13, createToggle(Material.STICK, "Reach", reach, "reach"));
        inv.setItem(14, createToggle(Material.FEATHER, "NoFall", nofall, "nofall"));

        inv.setItem(22, createButton(Material.ARROW, "<gray>← Назад", null));

        fillWithGlass(inv);
        player.openInventory(inv);
    }

    private ItemStack createToggle(Material mat, String name, boolean enabled, String path) {
        String status = enabled ? "<green>ВКЛ" : "<red>ВЫКЛ";
        return createButton(mat, "<white>" + name + " " + status,
                Collections.singletonList("<gold>ЛКМ <gray>- Переключить"));
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