package com.fassykite.polarisx.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public static MiniMessage miniMessage() {
        return MINI;
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            player.sendMessage(MINI.deserialize(message));
        } else {
            sender.sendMessage(MINI.serialize(MINI.deserialize(message)));
        }
    }

    public static void noPermission(CommandSender sender) {
        sendMessage(sender, "<red>Недостаточно прав!</red>");
    }
}