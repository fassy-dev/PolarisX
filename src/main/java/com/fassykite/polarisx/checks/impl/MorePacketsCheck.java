package com.fassykite.polarisx.checks.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.fassykite.polarisx.checks.AbstractCheck;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.config.ChecksConfig;
import com.fassykite.polarisx.utils.PlayerData;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MorePacketsCheck extends AbstractCheck {

    // Счётчики пакетов: UUID -> количество
    private final ConcurrentHashMap<java.util.UUID, Integer> packetCount = new ConcurrentHashMap<>();
    // Время последнего пакета
    private final ConcurrentHashMap<java.util.UUID, Long> lastPacketTime = new ConcurrentHashMap<>();

    public MorePacketsCheck() {
        super(CheckType.MOREPACKETS);
    }

    /**
     * Обработка входящего пакета от игрока
     */
    public void handlePacket(PlayerData data, PacketEvent event) {
        if (!isEnabled() || !data.shouldCheck()) {
            return;
        }

        java.util.UUID uuid = data.getUniqueId();
        Player player = data.getPlayer();
        if (player == null) return;

        // Игнорируем неигровые пакеты
        if (!isGamePacket(event.getPacketType())) {
            return;
        }

        // Обновляем счётчик
        packetCount.merge(uuid, 1, Integer::sum);
        lastPacketTime.put(uuid, System.currentTimeMillis());

        // Сбрасываем счётчик через 1 секунду
        getServer().getScheduler().runTaskLaterAsynchronously(
                getPlugin(),
                () -> packetCount.merge(uuid, -1, Math::max),
                20L // 1 сек = 20 тиков
        );

        // Проверяем лимит
        int currentCount = packetCount.getOrDefault(uuid, 0);
        int threshold = ChecksConfig.getInt("morepackets.threshold", 60);

        if (currentCount > threshold) {
            data.addViolation(getCheckName());
            handle(data, "пакетов/сек: " + currentCount + " (лимит: " + threshold + ")");
        }
    }

    /**
     * Проверяет, является ли пакет игровым (а не чатом, GUI и т.д.)
     */
    private boolean isGamePacket(PacketType type) {
        return type.getProtocol() == PacketType.Protocol.PLAY &&
                type.getSender() == PacketType.Sender.CLIENT &&
                (type == PacketType.Play.Client.POSITION ||
                        type == PacketType.Play.Client.POSITION_LOOK ||
                        type == PacketType.Play.Client.LOOK ||
                        type == PacketType.Play.Client.FLYING ||
                        type == PacketType.Play.Client.USE_ENTITY ||
                        type == PacketType.Play.Client.ARM_ANIMATION ||
                        type == PacketType.Play.Client.BLOCK_DIG ||
                        type == PacketType.Play.Client.BLOCK_PLACE);
    }

    @Override
    public void handle(PlayerData data, String reason) {
        alert(data, reason);
    }

    // Вспомогательный метод для получения инстанса плагина
    private org.bukkit.plugin.Plugin getPlugin() {
        return com.fassykite.polarisx.PolarisX.getInstance();
    }

    // Для совместимости с CheckType
    public enum CheckType {
        MOREPACKETS
    }
}