package com.fassykite.polarisx;

import com.fassykite.polarisx.checks.AbstractCheck;
import com.fassykite.polarisx.checks.CheckType;
import com.fassykite.polarisx.checks.impl.*;
import com.fassykite.polarisx.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PolarisManager {

    private static PolarisManager instance;
    private final PolarisX plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final Map<UUID, Map<CheckType, Integer>> violationsMap;
    private final List<AbstractCheck> activeChecks;
    private final Set<UUID> bypassPlayers;

    private boolean enabled = true;
    private int totalViolations = 0;
    private long lastCleanupTime = System.currentTimeMillis();

    public PolarisManager(PolarisX plugin) {
        instance = this;
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<>();
        this.violationsMap = new ConcurrentHashMap<>();
        this.activeChecks = new ArrayList<>();
        this.bypassPlayers = ConcurrentHashMap.newKeySet();

        initializeChecks();
        startCleanupTask();
        startCheckTask();
    }

    private void initializeChecks() {
        // Регистрация всех проверок
        registerCheck(new SpeedCheck(plugin));
        registerCheck(new FlyCheck(plugin));
        registerCheck(new KillAuraCheck(plugin));
        registerCheck(new ReachCheck(plugin));
        registerCheck(new NoFallCheck(plugin));
        registerCheck(new MorePacketsCheck(plugin));
        registerCheck(new TimerCheck(plugin));
        registerCheck(new ScaffoldCheck(plugin));
        registerCheck(new AutoClickerCheck(plugin));

        // Загрузка конфигурации проверок
        loadCheckConfigurations();

        plugin.getLogger().info("✅ Загружено " + activeChecks.size() + " проверок");
    }

    private void registerCheck(AbstractCheck check) {
        if (check.isEnabled()) {
            activeChecks.add(check);
            plugin.getLogger().info("  ✓ " + check.getName() + " (" + check.getType() + ")");
        } else {
            plugin.getLogger().info("  ✗ " + check.getName() + " (отключена)");
        }
    }

    private void loadCheckConfigurations() {
        // Загрузка настроек из checks.yml
        for (AbstractCheck check : activeChecks) {
            String checkName = check.getType().name().toLowerCase();

            // Получаем настройки из конфига
            int maxViolations = plugin.getConfigManager()
                    .getChecksConfig()
                    .getMaxViolations(checkName);

            boolean cancelEvent = plugin.getConfigManager()
                    .getChecksConfig()
                    .shouldCancelEvent(checkName);

            String punishCommand = plugin.getConfigManager()
                    .getChecksConfig()
                    .getPunishCommand(checkName);

            // Применяем настройки
            check.setMaxViolations(maxViolations);
            check.setCancelEvent(cancelEvent);
            check.setPunishmentCommand(punishCommand);
        }
    }

    public void addViolation(Player player, AbstractCheck check, String data) {
        if (!enabled || shouldBypass(player)) return;

        UUID uuid = player.getUniqueId();
        CheckType type = check.getType();

        // Увеличиваем счетчик нарушений
        int violations = incrementViolations(uuid, type);
        totalViolations++;

        // Получаем данные игрока
        PlayerData playerData = getPlayerData(player);

        // Записываем последнее нарушение
        playerData.setLastViolation(type, System.currentTimeMillis());
        playerData.setLastViolationData(data);

        // Отправляем алерты
        sendViolationAlert(player, check, violations, data);

        // Проверяем наказание
        if (violations >= check.getMaxViolations()) {
            executePunishment(player, check, violations);
            resetViolations(uuid, type);
        }

        // Логирование
        if (plugin.getConfigManager().getPolarisConfig().isLogViolations()) {
            logViolation(player, check, violations, data);
        }
    }

    private int incrementViolations(UUID uuid, CheckType type) {
        Map<CheckType, Integer> playerViolations = violationsMap
                .computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());

        return playerViolations.merge(type, 1, Integer::sum);
    }

    private void resetViolations(UUID uuid, CheckType type) {
        Map<CheckType, Integer> playerViolations = violationsMap.get(uuid);
        if (playerViolations != null) {
            playerViolations.remove(type);
            if (playerViolations.isEmpty()) {
                violationsMap.remove(uuid);
            }
        }
    }

    private void sendViolationAlert(Player player, AbstractCheck check, int violations, String data) {
        if (!plugin.getConfigManager().getPolarisConfig().isAlertsEnabled()) return;

        String alertMessage = plugin.getConfigManager().getMessagesConfig()
                .getAlertMessage()
                .replace("%player%", player.getName())
                .replace("%check%", check.getName())
                .replace("%type%", check.getType().getDisplayName())
                .replace("%vl%", String.valueOf(violations))
                .replace("%max%", String.valueOf(check.getMaxViolations()))
                .replace("%data%", data);

        // Отправляем админам с пермишеном
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("polarisx.alerts")) {
                com.fassykite.polarisx.utils.MessageUtils.sendMessage(onlinePlayer, alertMessage);

                // ActionBar уведомление
                if (plugin.getConfigManager().getPolarisConfig().isActionBarAlerts()) {
                    String actionBar = plugin.getConfigManager().getMessagesConfig()
                            .getActionBarAlert()
                            .replace("%player%", player.getName())
                            .replace("%check%", check.getName())
                            .replace("%vl%", String.valueOf(violations));

                    com.fassykite.polarisx.utils.MessageUtils.sendActionBar(onlinePlayer, actionBar);
                }
            }
        }

        // Отправка в консоль
        if (plugin.getConfigManager().getPolarisConfig().isConsoleAlerts()) {
            plugin.getLogger().warning("🚨 " + player.getName() + " failed " + check.getName() +
                    " (VL: " + violations + "/" + check.getMaxViolations() + ") Data: " + data);
        }
    }

    private void executePunishment(Player player, AbstractCheck check, int violations) {
        if (!plugin.getConfigManager().getPolarisConfig().isPunishmentsEnabled()) return;

        String command = check.getPunishmentCommand()
                .replace("%player%", player.getName())
                .replace("%check%", check.getName())
                .replace("%vl%", String.valueOf(violations))
                .replace("%uuid%", player.getUniqueId().toString());

        // Выполняем команду синхронно
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            // Броадкаст наказания
            if (plugin.getConfigManager().getPolarisConfig().isPunishmentBroadcast()) {
                String broadcast = plugin.getConfigManager().getMessagesConfig()
                        .getPunishmentBroadcast()
                        .replace("%player%", player.getName())
                        .replace("%check%", check.getName());

                Bukkit.broadcast(broadcast, "polarisx.alerts");
            }
        });

        // Сбрасываем нарушения после наказания
        resetViolations(player.getUniqueId(), check.getType());
    }

    private void logViolation(Player player, AbstractCheck check, int violations, String data) {
        // Логирование в файл (можно реализовать позже)
        String logEntry = String.format("[%s] %s failed %s (VL: %d) - %s",
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
                player.getName(),
                check.getName(),
                violations,
                data
        );

        // Пока просто в консоль
        plugin.getLogger().info("📝 " + logEntry);
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(),
                uuid -> new PlayerData(player));
    }

    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
        violationsMap.remove(uuid);
    }

    public void runAsyncChecks() {
        if (!enabled) return;

        long startTime = System.currentTimeMillis();
        int processedPlayers = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (shouldBypass(player)) continue;

            PlayerData data = getPlayerData(player);
            processedPlayers++;

            for (AbstractCheck check : activeChecks) {
                if (!check.isEnabled()) continue;

                try {
                    check.runAsyncCheck(player, data);
                } catch (Exception e) {
                    plugin.getLogger().severe("Ошибка в проверке " + check.getName() +
                            " для игрока " + player.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Производительность
        long checkTime = System.currentTimeMillis() - startTime;
        if (checkTime > 20 && plugin.getConfigManager().getPolarisConfig().isDebug()) {
            plugin.getLogger().warning("⚠ Проверки заняли " + checkTime + "мс (" +
                    processedPlayers + " игроков, " + activeChecks.size() + " проверок)");
        }
    }

    public void runSyncChecks() {
        if (!enabled) return;

        // Синхронные проверки (например, при событиях)
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (shouldBypass(player)) return;

            PlayerData data = getPlayerData(player);

            activeChecks.stream()
                    .filter(AbstractCheck::isEnabled)
                    .forEach(check -> {
                        // Здесь можно добавить синхронные проверки
                    });
        });
    }

    private void startCleanupTask() {
        // Очистка неактивных игроков каждые 5 минут
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTime = System.currentTimeMillis();

            playerDataMap.entrySet().removeIf(entry -> {
                UUID uuid = entry.getKey();
                Player player = Bukkit.getPlayer(uuid);

                // Удаляем данные если игрок оффлайн больше 10 минут
                if (player == null || !player.isOnline()) {
                    violationsMap.remove(uuid);
                    return true;
                }

                return false;
            });

            lastCleanupTime = currentTime;

        }, 6000L, 6000L); // 5 минут = 6000 тиков
    }

    private void startCheckTask() {
        // Асинхронные проверки каждые тик
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            runAsyncChecks();
        }, 1L, 1L);

        // Синхронные проверки каждые 2 тика
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            runSyncChecks();
        }, 2L, 2L);
    }

    public void reload() {
        activeChecks.clear();
        initializeChecks();
        plugin.getLogger().info("🔄 Менеджер проверок перезагружен");
    }

    public void enable() {
        enabled = true;
        plugin.getLogger().info("🟢 PolarisManager включен");
    }

    public void disable() {
        enabled = false;
        playerDataMap.clear();
        violationsMap.clear();
        bypassPlayers.clear();
        plugin.getLogger().info("🔴 PolarisManager выключен");
    }

    public void addBypass(Player player) {
        bypassPlayers.add(player.getUniqueId());
    }

    public void removeBypass(Player player) {
        bypassPlayers.remove(player.getUniqueId());
    }

    public boolean hasBypass(Player player) {
        return bypassPlayers.contains(player.getUniqueId()) ||
                player.hasPermission("polarisx.bypass");
    }

    private boolean shouldBypass(Player player) {
        return !enabled || hasBypass(player);
    }

    // ========== ГЕТТЕРЫ И СТАТИСТИКА ==========

    public static PolarisManager getInstance() {
        return instance;
    }

    public int getViolations(UUID uuid, CheckType type) {
        Map<CheckType, Integer> playerViolations = violationsMap.get(uuid);
        return playerViolations != null ? playerViolations.getOrDefault(type, 0) : 0;
    }

    public int getTotalViolations(UUID uuid) {
        Map<CheckType, Integer> playerViolations = violationsMap.get(uuid);
        if (playerViolations == null) return 0;

        return playerViolations.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getGlobalViolations() {
        return totalViolations;
    }

    public Map<CheckType, Integer> getPlayerViolations(UUID uuid) {
        return Collections.unmodifiableMap(
                violationsMap.getOrDefault(uuid, new HashMap<>())
        );
    }

    public List<AbstractCheck> getActiveChecks() {
        return Collections.unmodifiableList(activeChecks);
    }

    public List<AbstractCheck> getEnabledChecks() {
        return activeChecks.stream()
                .filter(AbstractCheck::isEnabled)
                .toList();
    }

    public int getOnlinePlayersWithData() {
        return playerDataMap.size();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getLastCleanupTime() {
        return lastCleanupTime;
    }

    public Set<UUID> getBypassPlayers() {
        return Collections.unmodifiableSet(bypassPlayers);
    }

    // ========== УТИЛИТНЫЕ МЕТОДЫ ==========

    public void resetPlayerViolations(UUID uuid) {
        violationsMap.remove(uuid);

        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            data.resetAllViolations();
        }
    }

    public void resetAllViolations() {
        violationsMap.clear();
        playerDataMap.values().forEach(PlayerData::resetAllViolations);
        totalViolations = 0;
    }

    public void disableCheck(CheckType type) {
        activeChecks.stream()
                .filter(check -> check.getType() == type)
                .findFirst()
                .ifPresent(check -> check.setEnabled(false));
    }

    public void enableCheck(CheckType type) {
        activeChecks.stream()
                .filter(check -> check.getType() == type)
                .findFirst()
                .ifPresent(check -> check.setEnabled(true));
    }

    public boolean isCheckEnabled(CheckType type) {
        return activeChecks.stream()
                .filter(check -> check.getType() == type)
                .findFirst()
                .map(AbstractCheck::isEnabled)
                .orElse(false);
    }

    public void setCheckMaxViolations(CheckType type, int maxViolations) {
        activeChecks.stream()
                .filter(check -> check.getType() == type)
                .findFirst()
                .ifPresent(check -> check.setMaxViolations(maxViolations));
    }

    // ========== DEBUG И ДИАГНОСТИКА ==========

    public void printDebugInfo() {
        plugin.getLogger().info("=== PolarisManager Debug ===");
        plugin.getLogger().info("Статус: " + (enabled ? "ВКЛ" : "ВЫКЛ"));
        plugin.getLogger().info("Игроков в памяти: " + playerDataMap.size());
        plugin.getLogger().info("Активных проверок: " + activeChecks.size());
        plugin.getLogger().info("Всего нарушений: " + totalViolations);
        plugin.getLogger().info("Игроков с байпасом: " + bypassPlayers.size());
        plugin.getLogger().info("===========================");
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("enabled", enabled);
        stats.put("total_players", playerDataMap.size());
        stats.put("total_violations", totalViolations);
        stats.put("active_checks", activeChecks.size());
        stats.put("bypass_players", bypassPlayers.size());
        stats.put("last_cleanup", lastCleanupTime);

        // Статистика по проверкам
        Map<String, Integer> checkStats = new HashMap<>();
        for (AbstractCheck check : activeChecks) {
            checkStats.put(check.getType().name(), check.getMaxViolations());
        }
        stats.put("checks", checkStats);

        return stats;
    }

    // ========== SERIALIZATION (для сохранения данных) ==========

    public void saveAllData() {
        // TODO: Реализовать сохранение данных в БД/файл
        plugin.getLogger().info("💾 Сохранение данных PolarisManager...");
    }

    public void loadAllData() {
        // TODO: Реализовать загрузку данных из БД/файла
        plugin.getLogger().info("📂 Загрузка данных PolarisManager...");
    }
}