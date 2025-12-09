package com.fassykite.polarisx.command;

import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class AlertsCommand implements CommandExecutor {

    private static final ConcurrentHashMap<java.util.UUID, Boolean> alertsEnabled = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 2000L;
    private static final ConcurrentHashMap<java.util.UUID, Long> cooldowns = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.sendMessage(sender, "<red>Эта команда только для игроков!</red>");
            return true;
        }

        if (!sender.hasPermission("polarisx.alerts")) {
            MessageUtils.noPermission(sender);
            return true;
        }

        if (isOnCooldown(player)) {
            MessageUtils.sendMessage(player, "<red>Подождите перед повторным использованием!</red>");
            return true;
        }
        setCooldown(player);

        boolean current = alertsEnabled.getOrDefault(player.getUniqueId(), true);
        alertsEnabled.put(player.getUniqueId(), !current);
        MessageUtils.sendMessage(player, (!current ? "<green>Уведомления включены</green>" : "<red>Уведомления выключены</red>"));
        return true;
    }

    private boolean isOnCooldown(Player player) {
        Long last = cooldowns.get(player.getUniqueId());
        return last != null && System.currentTimeMillis() - last < COOLDOWN_MS;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}