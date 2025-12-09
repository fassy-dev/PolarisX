package com.fassykite.polarisx;

import com.fassykite.polarisx.command.PolarisCommand;
import com.fassykite.polarisx.config.ConfigManager;
import com.fassykite.polarisx.api.PlaceholderAPIHook;
import com.fassykite.polarisx.api.PolarisExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class PolarisX extends JavaPlugin {

    // Статический экземпляр для доступа из других классов
    private static PolarisX instance;

    // Основные менеджеры
    private ConfigManager configManager;
    private PolarisManager polarisManager;

    // API интеграции
    private PlaceholderAPIHook papiHook;
    private PolarisExpansion expansion;

    // Задачи
    private BukkitTask autoSaveTask;
    private BukkitTask metricsTask;

    // Статусы
    private boolean enabled = true;
    private boolean debugMode = false;
    private boolean firstLoad = true;

    // Логгер
    private Logger fileLogger;
    private FileHandler fileHandler;

    // Константы
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "Fassykite";
    public static final String WEBSITE = "https://github.com/Fassykite/PolarisX";

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        // Устанавливаем статический экземпляр
        instance = this;

        try {
            // Создаем папку данных если её нет
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            // Инициализация логгера в файл
            setupFileLogger();

            // Показываем баннер загрузки
            printBanner();

            // Инициализация конфигурации
            configManager = new ConfigManager(this);
            configManager.loadConfigs();

            // Загружаем настройки
            loadSettings();

            // Инициализация менеджера
            polarisManager = new PolarisManager(this);

            // Регистрация команд
            registerCommands();

            // Регистрация событий
            registerEvents();

            // Инициализация API
            initializeAPI();

            // Запуск задач
            startTasks();

            // Запуск метрик
            startMetrics();

            // Успешная загрузка
            long loadTime = System.currentTimeMillis() - startTime;
            logToFile("INFO", "Плагин успешно загружен за " + loadTime + "мс");

            getLogger().info(ChatColor.GREEN + "✅ PolarisX v" + VERSION + " успешно запущен!");
            getLogger().info(ChatColor.GRAY + "   Проверок: " + ChatColor.YELLOW +
                    polarisManager.getActiveChecks().size());
            getLogger().info(ChatColor.GRAY + "   Статус: " +
                    (enabled ? ChatColor.GREEN + "АКТИВЕН" : ChatColor.RED + "ОТКЛЮЧЕН"));
            getLogger().info(ChatColor.GRAY + "   Режим отладки: " +
                    (debugMode ? ChatColor.YELLOW + "ВКЛ" : ChatColor.GRAY + "ВЫКЛ"));

        } catch (Exception e) {
            getLogger().severe("❌ Критическая ошибка при загрузке PolarisX!");
            getLogger().severe("Ошибка: " + e.getMessage());
            e.printStackTrace();

            logToFile("SEVERE", "Критическая ошибка при загрузке: " + e.getMessage());
            logToFile("SEVERE", "Стек вызовов: " + getStackTraceAsString(e));

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            getLogger().info(ChatColor.GRAY + "Завершение работы PolarisX...");

            // Сохраняем все данные
            if (polarisManager != null) {
                polarisManager.saveAllData();
            }

            // Отключаем PlaceholderAPI
            if (papiHook != null) {
                papiHook.unregister();
            }

            // Отключаем задачи
            if (autoSaveTask != null) {
                autoSaveTask.cancel();
            }

            if (metricsTask != null) {
                metricsTask.cancel();
            }

            // Закрываем логгер файла
            if (fileHandler != null) {
                fileHandler.close();
            }

            getLogger().info(ChatColor.RED + "🔴 PolarisX успешно выключен");
            logToFile("INFO", "Плагин выключен");

        } catch (Exception e) {
            getLogger().severe("Ошибка при выключении PolarisX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printBanner() {
        String[] banner = {
                "╔══════════════════════════════════════════════════════════════╗",
                "║                                                              ║",
                "║  " + ChatColor.AQUA + "██████╗  ██████╗ ██╗      █████╗ ██████╗ ██╗███████╗" + ChatColor.RESET + "    ║",
                "║  " + ChatColor.AQUA + "██╔══██╗██╔═══██╗██║     ██╔══██╗██╔══██╗██║██╔════╝" + ChatColor.RESET + "    ║",
                "║  " + ChatColor.AQUA + "██████╔╝██║   ██║██║     ███████║██████╔╝██║███████╗" + ChatColor.RESET + "    ║",
                "║  " + ChatColor.AQUA + "██╔═══╝ ██║   ██║██║     ██╔══██║██╔══██╗██║╚════██║" + ChatColor.RESET + "    ║",
                "║  " + ChatColor.AQUA + "██║     ╚██████╔╝███████╗██║  ██║██║  ██║██║███████║" + ChatColor.RESET + "    ║",
                "║  " + ChatColor.AQUA + "╚═╝      ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝╚══════╝" + ChatColor.RESET + "    ║",
                "║                                                              ║",
                "║  " + ChatColor.GOLD + "         Advanced Anti-Cheat System v" + VERSION + "           " + ChatColor.RESET + "║",
                "║  " + ChatColor.GRAY + "             Created by " + AUTHOR + "                 " + ChatColor.RESET + "║",
                "║  " + ChatColor.DARK_GRAY + "              " + WEBSITE + "                " + ChatColor.RESET + "║",
                "║                                                              ║",
                "╚══════════════════════════════════════════════════════════════╝"
        };

        for (String line : banner) {
            getLogger().info(line);
        }
    }

    private void setupFileLogger() {
        try {
            // Создаем папку для логов если её нет
            File logsFolder = new File(getDataFolder(), "logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdirs();
            }

            // Формат имени файла: polaris-YYYY-MM-dd.log
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(new Date());
            File logFile = new File(logsFolder, "polaris-" + date + ".log");

            // Настраиваем FileHandler
            fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            // Создаем отдельный логгер для файла
            fileLogger = Logger.getLogger("PolarisX-FileLogger");
            fileLogger.addHandler(fileHandler);
            fileLogger.setUseParentHandlers(false);

        } catch (Exception e) {
            getLogger().warning("Не удалось создать файловый логгер: " + e.getMessage());
        }
    }

    private void loadSettings() {
        // Загружаем настройки из конфига
        debugMode = getConfig().getBoolean("settings.debug", false);
        enabled = getConfig().getBoolean("settings.enabled", true);

        if (debugMode) {
            getLogger().setLevel(Level.ALL);
            getLogger().info(ChatColor.YELLOW + "⚡ Режим отладки включен!");
        }

        logToFile("INFO", "Настройки загружены. Debug: " + debugMode + ", Enabled: " + enabled);
    }

    private void registerCommands() {
        try {
            // Регистрация основной команды
            PolarisCommand polarisCommand = new PolarisCommand(this);
            getCommand("polaris").setExecutor(polarisCommand);
            getCommand("polaris").setTabCompleter(polarisCommand);

            // Регистрация дополнительных команд
            getCommand("ac").setExecutor(polarisCommand);
            getCommand("ac").setTabCompleter(polarisCommand);

            getLogger().info("✅ Команды зарегистрированы");

        } catch (Exception e) {
            getLogger().severe("❌ Ошибка при регистрации команд: " + e.getMessage());
            logToFile("SEVERE", "Ошибка регистрации команд: " + e.getMessage());
        }
    }

    private void registerEvents() {
        try {
            // Регистрация всех слушателей событий
            Bukkit.getPluginManager().registerEvents(
                    new com.fassykite.polarisx.events.PlayerListener(this), this);

            Bukkit.getPluginManager().registerEvents(
                    new com.fassykite.polarisx.events.GUIListener(), this);

            Bukkit.getPluginManager().registerEvents(
                    new com.fassykite.polarisx.events.PacketListener(this), this);

            getLogger().info("✅ События зарегистрированы");

        } catch (Exception e) {
            getLogger().severe("❌ Ошибка при регистрации событий: " + e.getMessage());
            logToFile("SEVERE", "Ошибка регистрации событий: " + e.getMessage());
        }
    }

    private void initializeAPI() {
        // PlaceholderAPI интеграция
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                papiHook = new PlaceholderAPIHook(this);
                papiHook.register();

                expansion = new PolarisExpansion(this);
                if (expansion.register()) {
                    getLogger().info(ChatColor.GREEN + "✅ PlaceholderAPI расширение зарегистрировано");
                    logToFile("INFO", "PlaceholderAPI расширение зарегистрировано");
                }

            } catch (Exception e) {
                getLogger().warning("⚠ Не удалось зарегистрировать PlaceholderAPI: " + e.getMessage());
                logToFile("WARNING", "PlaceholderAPI ошибка: " + e.getMessage());
            }
        } else {
            getLogger().info(ChatColor.YELLOW + "⚠ PlaceholderAPI не найден, плейсхолдеры отключены");
        }

        // ProtocolLib интеграция (если нужно)
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            getLogger().info(ChatColor.GREEN + "✅ ProtocolLib обнаружен, пакетные проверки доступны");
            logToFile("INFO", "ProtocolLib обнаружен");
        }
    }

    private void startTasks() {
        try {
            // Авто-сохранение данных каждые 5 минут
            autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                if (polarisManager != null) {
                    polarisManager.saveAllData();
                    logToFile("INFO", "Авто-сохранение выполнено");
                }
            }, 6000L, 6000L); // 5 минут

            // Очистка старых данных каждые 30 минут
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                cleanupOldData();
            }, 36000L, 36000L); // 30 минут

            getLogger().info("✅ Задачи запущены");

        } catch (Exception e) {
            getLogger().severe("❌ Ошибка при запуске задач: " + e.getMessage());
            logToFile("SEVERE", "Ошибка запуска задач: " + e.getMessage());
        }
    }

    private void startMetrics() {
        try {
            // Метрики производительности
            metricsTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
                if (!debugMode) return;

                // Собираем метрики
                int tps = (int) Bukkit.getTPS()[0];
                int players = Bukkit.getOnlinePlayers().size();
                int checks = polarisManager != null ? polarisManager.getActiveChecks().size() : 0;
                int violations = polarisManager != null ? polarisManager.getGlobalViolations() : 0;

                logToFile("PERF", String.format(
                        "TPS: %d, Игроки: %d, Проверки: %d, Нарушения: %d",
                        tps, players, checks, violations
                ));

            }, 1200L, 1200L); // Каждую минуту

        } catch (Exception e) {
            getLogger().warning("Не удалось запустить метрики: " + e.getMessage());
        }
    }

    private void cleanupOldData() {
        try {
            // Очистка старых лог файлов (старше 7 дней)
            File logsFolder = new File(getDataFolder(), "logs");
            if (logsFolder.exists() && logsFolder.isDirectory()) {
                File[] logFiles = logsFolder.listFiles((dir, name) ->
                        name.startsWith("polaris-") && name.endsWith(".log"));

                if (logFiles != null) {
                    long weekAgo = System.currentTimeMillis() - (7L * 24L * 60L * 60L * 1000L);

                    for (File logFile : logFiles) {
                        if (logFile.lastModified() < weekAgo) {
                            if (logFile.delete()) {
                                logToFile("INFO", "Удален старый лог файл: " + logFile.getName());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logToFile("WARNING", "Ошибка очистки старых данных: " + e.getMessage());
        }
    }

    // ========== ПУБЛИЧНЫЕ МЕТОДЫ ==========

    /**
     * Перезагрузить плагин
     */
    public void reload() {
        try {
            getLogger().info(ChatColor.GOLD + "🔄 Перезагрузка PolarisX...");

            // Перезагружаем конфигурацию
            configManager.reloadConfigs();

            // Перезагружаем настройки
            loadSettings();

            // Перезагружаем менеджер проверок
            if (polarisManager != null) {
                polarisManager.reload();
            }

            getLogger().info(ChatColor.GREEN + "✅ PolarisX успешно перезагружен!");
            logToFile("INFO", "Плагин перезагружен");

        } catch (Exception e) {
            getLogger().severe("❌ Ошибка при перезагрузке: " + e.getMessage());
            logToFile("SEVERE", "Ошибка перезагрузки: " + e.getMessage());
        }
    }

    /**
     * Включить/выключить плагин
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (polarisManager != null) {
            if (enabled) {
                polarisManager.enable();
            } else {
                polarisManager.disable();
            }
        }

        String status = enabled ? "включен" : "выключен";
        getLogger().info(ChatColor.YELLOW + (enabled ? "🟢" : "🔴") +
                " PolarisX " + status);
        logToFile("INFO", "Плагин " + status);
    }

    /**
     * Включить/выключить режим отладки
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;

        if (debugMode) {
            getLogger().setLevel(Level.ALL);
            getLogger().info(ChatColor.YELLOW + "⚡ Режим отладки включен!");
        } else {
            getLogger().setLevel(Level.INFO);
            getLogger().info(ChatColor.GRAY + "⚡ Режим отладки выключен");
        }

        logToFile("INFO", "Режим отладки: " + (debugMode ? "включен" : "выключен"));
    }

    /**
     * Логирование в файл
     */
    public void logToFile(String level, String message) {
        if (fileLogger != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String time = timeFormat.format(new Date());

            String logMessage = String.format("[%s] [%s] %s", time, level, message);

            switch (level.toUpperCase()) {
                case "SEVERE":
                    fileLogger.severe(logMessage);
                    break;
                case "WARNING":
                    fileLogger.warning(logMessage);
                    break;
                case "INFO":
                    fileLogger.info(logMessage);
                    break;
                case "PERF":
                    fileLogger.info("[PERF] " + logMessage);
                    break;
                default:
                    fileLogger.info(logMessage);
            }
        }
    }

    /**
     * Получить стек вызовов как строку
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    // ========== ГЕТТЕРЫ ==========

    /**
     * Получить статический экземпляр плагина
     */
    public static PolarisX getInstance() {
        return instance;
    }

    /**
     * Получить менеджер конфигурации
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Получить менеджер проверок
     */
    public PolarisManager getPolarisManager() {
        return polarisManager;
    }

    /**
     * Получить статус плагина
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Получить режим отладки
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Проверить первую загрузку
     */
    public boolean isFirstLoad() {
        return firstLoad;
    }

    /**
     * Получить PlaceholderAPI хук
     */
    public PlaceholderAPIHook getPapiHook() {
        return papiHook;
    }

    /**
     * Получить PlaceholderAPI расширение
     */
    public PolarisExpansion getExpansion() {
        return expansion;
    }

    /**
     * Получить версию плагина
     */
    public String getPluginVersion() {
        return VERSION;
    }

    /**
     * Получить автора плагина
     */
    public String getPluginAuthor() {
        return AUTHOR;
    }

    // ========== УТИЛИТНЫЕ МЕТОДЫ ==========

    /**
     * Отправить сообщение в консоль с префиксом
     */
    public void sendConsoleMessage(String message) {
        getLogger().info(ChatColor.translateAlternateColorCodes('&',
                "&8[&bPolarisX&8] &7" + message));
    }

    /**
     * Отправить предупреждение в консоль
     */
    public void sendConsoleWarning(String message) {
        getLogger().warning(ChatColor.translateAlternateColorCodes('&',
                "&8[&bPolarisX&8] &e" + message));
    }

    /**
     * Отправить ошибку в консоль
     */
    public void sendConsoleError(String message) {
        getLogger().severe(ChatColor.translateAlternateColorCodes('&',
                "&8[&bPolarisX&8] &c" + message));
    }

    /**
     * Проверить, работает ли плагин
     */
    public boolean isRunning() {
        return enabled && polarisManager != null && polarisManager.isEnabled();
    }

    /**
     * Получить информацию о плагине
     */
    public String getPluginInfo() {
        return String.format(
                "PolarisX v%s by %s\n" +
                        "Статус: %s\n" +
                        "Проверок: %d\n" +
                        "Нарушений: %d\n" +
                        "Режим отладки: %s",
                VERSION, AUTHOR,
                enabled ? "Активен" : "Отключен",
                polarisManager != null ? polarisManager.getActiveChecks().size() : 0,
                polarisManager != null ? polarisManager.getGlobalViolations() : 0,
                debugMode ? "Включен" : "Выключен"
        );
    }

    /**
     * Создать дамп данных для отладки
     */
    public String createDebugDump() {
        StringBuilder dump = new StringBuilder();
        dump.append("=== PolarisX Debug Dump ===\n");
        dump.append("Время: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        dump.append("Версия: ").append(VERSION).append("\n");
        dump.append("Статус: ").append(enabled ? "Enabled" : "Disabled").append("\n");
        dump.append("Debug Mode: ").append(debugMode).append("\n");
        dump.append("\n");

        if (polarisManager != null) {
            dump.append("=== PolarisManager ===\n");
            dump.append("Active Checks: ").append(polarisManager.getActiveChecks().size()).append("\n");
            dump.append("Online Players: ").append(polarisManager.getOnlinePlayersWithData()).append("\n");
            dump.append("Global Violations: ").append(polarisManager.getGlobalViolations()).append("\n");
            dump.append("Bypass Players: ").append(polarisManager.getBypassPlayers().size()).append("\n");
        }

        if (configManager != null) {
            dump.append("\n=== ConfigManager ===\n");
            dump.append("Config Loaded: ").append("Yes").append("\n");
        }

        dump.append("\n=== Server Info ===\n");
        dump.append("Bukkit Version: ").append(Bukkit.getBukkitVersion()).append("\n");
        dump.append("Online Players: ").append(Bukkit.getOnlinePlayers().size()).append("\n");
        dump.append("TPS: ").append(String.format("%.2f", Bukkit.getTPS()[0])).append("\n");

        return dump.toString();
    }

    /**
     * Сохранить дамп в файл
     */
    public void saveDebugDumpToFile() {
        try {
            File debugFolder = new File(getDataFolder(), "debug");
            if (!debugFolder.exists()) {
                debugFolder.mkdirs();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String fileName = "debug-dump-" + dateFormat.format(new Date()) + ".txt";
            File dumpFile = new File(debugFolder, fileName);

            String dump = createDebugDump();
            java.nio.file.Files.write(dumpFile.toPath(), dump.getBytes());

            getLogger().info("✅ Дамп отладки сохранен в: " + dumpFile.getAbsolutePath());
            logToFile("INFO", "Дамп отладки сохранен: " + fileName);

        } catch (Exception e) {
            getLogger().warning("Не удалось сохранить дамп отладки: " + e.getMessage());
        }
    }
}