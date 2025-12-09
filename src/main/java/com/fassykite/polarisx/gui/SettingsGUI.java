package com.fassykite.polarisx.gui;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.config.MessagesConfig;
import com.fassykite.polarisx.config.SettingsConfig;
import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SettingsGUI {

    private final PolarisX plugin;

    public SettingsGUI(PolarisX plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        if (!player.hasPermission("polarisx.admin")) {
            MessageUtils.noPermission(player);
            return;
        }

        String title = MessagesConfig.getRaw("gui-title-settings");
        Inventory inv = Bukkit.createInventory(null, 54,
                MessageUtils.miniMessage().deserialize(title));

        fillBorders(inv);
        addSettingItems(inv);
        addControlItems(inv);

        player.openInventory(inv);
        if (SettingsConfig.getBoolean("gui.sounds", true)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        }
    }

    private void addSettingItems(Inventory inv) {
        inv.setItem(10, createToggleItem(Material.BELL, "Уведомления",
                SettingsConfig.getBoolean("alerts.enabled", true),
                Arrays.asList(
                        "<gray>Включить/выключить уведомления о нарушениях",
                        "",
                        "<gold>ЛКМ <gray>- Переключить"
                )));

        inv.setItem(11, createToggleItem(Material.ANVIL, "Наказания",
                SettingsConfig.getBoolean("punishments.enabled", true),
                Collections.singletonList("<gold>ЛКМ <gray>- Переключить")));

        inv.setItem(12, createToggleItem(Material.NAME_TAG, "ActionBar уведомления",
                SettingsConfig.getBoolean("alerts.actionbar", false),
                Collections.singletonList("<gold>ЛКМ <gray>- Переключить")));

        inv.setItem(13, createToggleItem(Material.PAINTING, "Градиенты в GUI",
                SettingsConfig.getBoolean("gui.gradients", true),
                Collections.singletonList("<gold>ЛКМ <gray>- Переключить")));

        inv.setItem(14, createToggleItem(Material.BOOK, "Логирование нарушений",
                SettingsConfig.getBoolean("logging.enabled", true),
                Collections.singletonList("<gold>ЛКМ <gray>- Переключить")));

        inv.setItem(15, createToggleItem(Material.COMMAND_BLOCK, "Уведомления в консоль",
                SettingsConfig.getBoolean("alerts.console", true),
                Collections.singletonList("<gold>ЛКМ <gray>- Переключить")));

        inv.setItem(16, createButtonItem(Material.CLOCK, "⚡ Производительность", null));
        inv.setItem(19, createButtonItem(Material.REDSTONE, "⚖ Система наказаний", null));
        inv.setItem(20, createButtonItem(Material.COMPARATOR, "🔧 Настройки проверок", null));
        inv.setItem(21, createButtonItem(Material.CHEST, "💾 База данных", null));
        inv.setItem(22, createButtonItem(Material.PAPER, "💬 Сообщения", null));
        inv.setItem(23, createButtonItem(Material.REDSTONE_TORCH, "🔗 Интеграции", null));
        inv.setItem(24, createButtonItem(Material.IRON_DOOR, "🔒 Безопасность", null));
        inv.setItem(25, createButtonItem(Material.MAP, "📤 Экспорт/Импорт", null));
    }

    private void addControlItems(Inventory inv) {
        inv.setItem(48, createButtonItem(Material.EMERALD, "<green>💾 Сохранить настройки", null));
        inv.setItem(49, createButtonItem(Material.REDSTONE_BLOCK, "<red>🔄 Сбросить настройки",
                Arrays.asList("<gray>Сброс к значениям по умолчанию")));
        inv.setItem(50, createButtonItem(Material.ARROW, "<gray>← Назад", null));
    }

    private ItemStack createToggleItem(Material mat, String name, boolean enabled, List<String> lore) {
        String status = enabled ? "<green>ВКЛ" : "<red>ВЫКЛ";
        String displayName = "<gradient:#4A90E2:#9013FE>" + name + "</gradient> " + status;
        return createItem(mat, displayName, lore);
    }

    private ItemStack createButtonItem(Material mat, String name, List<String> lore) {
        String displayName = "<gradient:#4A90E2:#9013FE>" + name + "</gradient>";
        return createItem(mat, displayName, lore);
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
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

    private void fillBorders(Inventory inv) {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.displayName(MessageUtils.miniMessage().deserialize("<dark_gray>"));
        border.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }
    }
}