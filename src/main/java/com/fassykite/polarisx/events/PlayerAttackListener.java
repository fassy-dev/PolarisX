package com.fassykite.polarisx.events;

import com.fassykite.polarisx.PolarisX;
import com.fassykite.polarisx.checks.impl.KillAuraCheck;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

public class PlayerAttackListener implements Listener {

    private final ConcurrentHashMap<java.util.UUID, List<Long>> attackHistory = new ConcurrentHashMap<>();

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        java.util.UUID id = player.getUniqueId();
        attackHistory.computeIfAbsent(id, k -> new ArrayList<>()).add(System.currentTimeMillis());
        List<Long> history = attackHistory.get(id);
        history.removeIf(time -> System.currentTimeMillis() - time > 1000);

        KillAuraCheck check = (KillAuraCheck) PolarisX.getInstance().getManager().getCheck("killaura");
        if (check != null) {
            check.check(player, history.size());
        }
    }
}