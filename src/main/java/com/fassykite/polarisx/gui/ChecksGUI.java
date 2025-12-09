package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ChecksGUI {

    private final File configFile = new File("plugins/PolarisX/settings.yml");

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27,
                MessageUtils.miniMessage().deserialize("<gradient:#4A90E2:#9013FE>Проверки</gradient>")
        );

        // Speed
        setCheckItem(inv, 10, "speed", "Speed", Material.FEATHER);
        // Fly
        setCheckItem(inv, 11, "fly", "Fly", Material.ELYTRA);
        // KillAura
        setCheckItem(inv, 12, "killaura", "KillAura", Material.DIAMOND_SWORD);
        // Reach
        setCheckItem(inv, 13, "reach", "Reach", Material.STICK);
        // NoFall
        setCheckItem(inv, 14, "nofall", "NoFall", Material.FEATHER);

        // Назад
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(MessageUtils.miniMessage().deserialize("<red>← Назад"));
        back.setItemMeta(backMeta);
        inv.setItem(22, back);

        // Стекло
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

    private void setCheckItem(Inventory inv, int slot, String key, String name, Material mat) {
        FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);
        boolean enabled = config.getBoolean("checks." + key + ".enabled", true);
        String action = config.getString("checks." + key + ".action", "warn");

        String status = enabled ? "<green>ВКЛ" : "<red>ВЫКЛ";
        String actionColor = switch (action) {
            case "ban" -> "<red>BAN";
            case "kick" -> "<gold>KICK";
            default -> "<aqua>WARN";
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtils.miniMessage().deserialize("<white>" + name + " " + status));
        meta.lore(Arrays.asList(
                MessageUtils.miniMessage().deserialize("<gray>Действие при нарушении: " + actionColor),
                MessageUtils.miniMessage().deserialize(""),
                MessageUtils.miniMessage().deserialize("<gold>ЛКМ</gold><gray> — Переключить вкл/выкл"),
                MessageUtils.miniMessage().deserialize("<gold>ПКМ</gold><gray> — Следующее действие")
        ));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void handleLeftClick(Player player, String checkKey) {
        try {
            org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);
            boolean current = config.getBoolean("checks." + checkKey + ".enabled", true);
            config.set("checks." + checkKey + ".enabled", !current);
            config.save(configFile);
            player.sendMessage(MessageUtils.miniMessage().deserialize(
                    "<green>Чек '" + checkKey + "' " + (!current ? "включён" : "выключен")
            ));
            open(player);
        } catch (IOException e) {
            player.sendMessage(MessageUtils.miniMessage().deserialize("<red>Ошибка сохранения!"));
        }
    }

    public void handleRightClick(Player player, String checkKey) {
        try {
            org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);
            String current = config.getString("checks." + checkKey + ".action", "warn");
            String next = switch (current) {
                case "warn" -> "kick";
                case "kick" -> "ban";
                case "ban" -> "warn";
                default -> "warn";
            };
            config.set("checks." + checkKey + ".action", next);
            config.save(configFile);
            player.sendMessage(MessageUtils.miniMessage().deserialize(
                    "<green>Для '" + checkKey + "' установлено действие: " + next.toUpperCase()
            ));
            open(player);
        } catch (IOException e) {
            player.sendMessage(MessageUtils.miniMessage().deserialize("<red>Ошибка сохранения!"));
        }
    }
}