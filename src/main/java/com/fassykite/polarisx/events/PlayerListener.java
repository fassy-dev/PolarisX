package com.fassykite.polarisx.events;

import com.fassykite.polarisx.PolarisX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Инициализация
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // Очистка
    }
}