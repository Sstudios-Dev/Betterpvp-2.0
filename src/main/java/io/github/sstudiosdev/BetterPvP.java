package io.github.sstudiosdev;

import io.github.sstudiosdev.command.*;
import io.github.sstudiosdev.listener.AntiKillAbuseListener;
import io.github.sstudiosdev.listener.PlayerDeathListener;
import io.github.sstudiosdev.listener.RespawnProtectionListener;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.CommandMapUtil;
import io.github.sstudiosdev.util.ErrorHandler;
import io.github.sstudiosdev.util.constructors.Config;
import io.github.sstudiosdev.util.constructors.SoundConfig;
import io.github.sstudiosdev.vault.VaultHookManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Main class of the BetterPvP plugin.
 */
public final class BetterPvP extends JavaPlugin {

    public static String prefix;
    @Getter
    private Config mainConfig;

    @Getter
    private SoundConfig soundConfig;

    @Getter
    private VaultHookManager vaultHookManager;
    private boolean loadLicenseFile;

    private String currentVersion;
    private String latestVersion;

    private static final String PLUGIN_VERSION = "1.0.6";

    private static final List<String> incompatiblePlugins = Arrays.asList(
            "epicplugin-1.0",
            "epicplugin-1.1",
            "epicplugin-1.2",
            "epicplugin-1.3",
            "epicplugin-1.4",
            "epicplugin-1.5",
            "epicplugin-1.6",
            "epicplugin-1.7",
            "epicplugin-1.8",
            "epicplugin-1.9",
            "epicplugin-1.10",
            "epicplugin-1.11",
            "EpicPremium-1.0",
            "EpicPremium-1.1",
            "EpicPremium-1.2",
            "EpicPremium-1.4",
            "EpicPremium-1.0.5",
            "EpicPremium-1.5.1",
            "EpicPremium-1.5.2",
            "EpicPremium-1.5.3"
    );

    /**
     * Method called when the plugin is enabled.
     */
    @Override
    public void onEnable() {

        // Load configurations
        loadConfigurations();

        // Log events
        registerEvents();

        // Register managers
        loadManagers();

        // Display information in the console
        displayConsoleInfo();

        checkForIncompatiblePlugins();

        // errorHandler
        Thread.setDefaultUncaughtExceptionHandler(new ErrorHandler(this));

        currentVersion = PLUGIN_VERSION;
        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);

        // Copy Apache-2.0 license file from resources to plugin folder
        if (mainConfig.getBoolean("load-license-file")) {
            try {
                File licenseFile = new File(getDataFolder(), "Apache-2.0 license");
                if (!licenseFile.exists()) {
                    InputStream inputStream = getResource("Apache-2.0 license");
                    Files.copy(inputStream, licenseFile.toPath());
                    getLogger().info("License file 'Apache-2.0' loaded successfully.");
                } else {
                    getLogger().info("License file 'Apache-2.0' already exists.");
                }
            } catch (IOException e) {
                getLogger().warning("Failed to load license file 'Apache-2.0'. Reason: " + e.getMessage());
            }
        }
    }

    /**
     * Method called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        sendMessageToConsole("&3BetterPvP is being disabled: this does not affect anything.");
        sendMessageToConsole("&7Goodbye!");
    }

    /**
     * Load the managers necessary for the plugin to work.
     */
    private void loadManagers() {
        vaultHookManager = new VaultHookManager();
    }

    /**
     * Load the plugin settings.
     */
    private void loadConfigurations() {
        // Initializing the Config Class for config.yml
        mainConfig = new Config(this, "config");
        mainConfig.load();

        // Initializing the SoundConfig Class for sound.yml
        soundConfig = new SoundConfig(this, "sound");
        soundConfig.load();

        // Get prefix from configuration
        prefix = mainConfig.getString("prefix");

        // Check if the plugin should load the license file
        if (mainConfig.contains("load-license-file")) {
            loadLicenseFile = mainConfig.getBoolean("load-license-file");
        } else {
            // Default value if not specified in config
            loadLicenseFile = true;
        }
    }

    /**
     * Log plugin events.
     */
    private void registerEvents() {
        // Register event listeners
        getServer().getPluginManager().registerEvents(new AntiKillAbuseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new RespawnProtectionListener(this), this);

        CommandMapUtil.registerCommand(this, new BetterPvPReloadCommand(this));
        CommandMapUtil.registerCommand(this, new BetterPvPNoPvPCommand(this));
        CommandMapUtil.registerCommand(this, new PvPWorldCommand(this));
        CommandMapUtil.registerCommand(this, new BtrMain(this));
        CommandMapUtil.registerCommand(this, new BetterPvPRegionCommand(this));
    }

    /**
     * Displays information in the console when you enable the plugin.
     */
    private void displayConsoleInfo() {
        sendMessageToConsole("     &3_____");
        sendMessageToConsole("   &3|   __  \\  &3BetterPvP &7v1.0.6-Stable       ");
        sendMessageToConsole("   &3|  |  | |  &7Running on Bukkit - Paper  ");
        sendMessageToConsole("   &3|  |___  |    &fPlugin by &3[srstaff_tv, sstudios, 1vcb, Updated by pichema and more]");
        sendMessageToConsole("   &3|  |__| |  ");
        sendMessageToConsole("   &3|_____ /   ");
        sendMessageToConsole("");
        sendMessageToConsole("&7Commands successfully loaded");
        sendMessageToConsole("&7Thank you &c pichema");
    }

    /**
     * Send a formatted message to the console.
     *
     * @param message Message to be sent to the console.
     */
    private void sendMessageToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColorUtil.colorize(message));
    }

    private void checkForUpdates() {
        try {
            URL url = new URL("https://api.github.com/repos/Sstudios-Dev/Betterpvp-2.0/releases/latest");

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            latestVersion = response.toString().split("\"tag_name\":\"")[1].split("\",")[0];

            if (isNewVersionAvailable()) {
                notifyPlayers();
            } else {
                getLogger().info("No updates are available. Current version: " + currentVersion);
            }
        } catch (IOException e) {
            getLogger().warning("Error checking for updates: " + e.getMessage());
        }
    }

    private boolean isNewVersionAvailable() {
        return latestVersion != null && !latestVersion.equalsIgnoreCase(currentVersion);
    }

    private void notifyPlayers() {
        getLogger().info("A new version of BetterPvP is available! Current version: " + currentVersion + ", Latest version: " + latestVersion);

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("A new version of BetterPvP is available! Current version: " + currentVersion + ", Latest version: " + latestVersion));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Notify the player who joined about incompatible plugins
        checkForIncompatiblePlugins();
    }

    private void checkForIncompatiblePlugins() {
        File pluginFolder = new File("plugins");
        File[] files = pluginFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                getLogger().info("Verifying plugin: " + file.getName());
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    String fileName = file.getName().replace(".jar", "");
                    if (incompatiblePlugins.contains(fileName)) {
                        String incompatiblePlugins = mainConfig.getString("incompatible-plugins");
                        incompatiblePlugins = incompatiblePlugins.replace("%plugin-incompatible%", fileName);

                        notifyPlayers(incompatiblePlugins);
                        getLogger().info(incompatiblePlugins);
                    }
                }
            }
        }
    }

    private void notifyPlayers(String message) {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            getLogger().info(message);
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(message);
                }
            }
        }
    }

}
