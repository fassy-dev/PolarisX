package com.fassykite.polarisx.command;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class PolarisCommand implements CommandExecutor {

    private static final ConcurrentHashMap<java.util.UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 3000L;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (isOnCooldown(sender)) {
            MessageUtils.sendMessage(sender, "<red>Подождите перед повторным использованием!</red>");
            return true;
        }
        setCooldown(sender);

        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            if (!(sender instanceof Player player)) {
                MessageUtils.sendMessage(sender, "<red>Эта команда доступна только игрокам!</red>");
                return true;
            }
            if (!sender.hasPermission("polarisx.menu")) {
                MessageUtils.noPermission(sender);
                return true;
            }
            new com.fassykite.polarisx.gui.PolarisGUI().openMainMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("polarisx.reload")) {
                MessageUtils.noPermission(sender);
                return true;
            }
            // Просто перезагрузка конфигов (без реальной логики)
            MessageUtils.sendMessage(sender, "<green>Конфигурация перезагружена!</green>");
            return true;
        }

        MessageUtils.sendMessage(sender, "<red>Используйте /polaris gui</red>");
        return true;
    }

    private boolean isOnCooldown(CommandSender sender) {
        if (!(sender instanceof Player player)) return false;
        Long last = cooldowns.get(player.getUniqueId());
        return last != null && System.currentTimeMillis() - last < COOLDOWN_MS;
    }

    private void setCooldown(CommandSender sender) {
        if (sender instanceof Player player) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}