package io.github.sstudiosdev;

import io.github.sstudiosdev.command.BetterPvPNoPvPCommand;
import io.github.sstudiosdev.command.BetterPvPReloadCommand;
import io.github.sstudiosdev.command.BtrMain;
import io.github.sstudiosdev.command.PvPWorldCommand;
import io.github.sstudiosdev.listener.AntiKillAbuseListener;
import io.github.sstudiosdev.listener.PlayerDeathListener;
import io.github.sstudiosdev.listener.RespawnProtectionListener;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.CommandMapUtil;
import io.github.sstudiosdev.util.constructors.Config;
import io.github.sstudiosdev.vault.VaultHookManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;

/**
 * Main class of the BetterPvP plugin.
 */
public final class BetterPvP extends JavaPlugin {

    public static String prefix;
    @Getter
    private Config mainConfig;

    @Getter
    private VaultHookManager vaultHookManager;
    private boolean loadLicenseFile;

    /**
     * Method called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        // Load configuration
        loadConfiguration();

        // Log events
        registerEvents();

        // Register managers
        loadManagers();

        // Display information in the console
        displayConsoleInfo();

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
    private void loadConfiguration() {
        // Initializing the Config Class
        mainConfig = new Config(this, "config");
        mainConfig.load();

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
    }

    /**
     * Displays information in the console when you enable the plugin.
     */
    private void displayConsoleInfo() {
        sendMessageToConsole("     &3_____");
        sendMessageToConsole("   &3|   __  \\  &3BetterPvP &7v1.0.0-Stable       ");
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

}
