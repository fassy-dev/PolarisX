package com.fassykite.polarisx.events;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.checks.impl.SpeedCheck;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().distanceSquared(e.getTo()) < 0.0001) return;
        double speed = e.getTo().distance(e.getFrom());
        SpeedCheck check = (SpeedCheck) PolarisX.getInstance().getManager().getCheck("speed");
        if (check != null) check.check(e.getPlayer(), speed);
    }
}