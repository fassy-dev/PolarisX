package com.fassykite.polarisx.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageUtils {
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public static MiniMessage mini() { return MINI; }
    public static Component color(String msg) { return MINI.deserialize(msg); }
    public static Component noPermission() { return color("<red>Нет прав!</red>"); }
}