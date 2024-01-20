/**
 * Main plugin for BetterPvP.
 * This plugin improves the PvP experience on Minecraft Bukkit servers.
 */
package github.betterpvp;

import Listener.AntiKillAbuseListener;
import Listener.PlayerDeathListener;
import commands.nopvp;
import commands.reload;
import manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import utils.ChatUtils;

/**
 * Main class that extends JavaPlugin, represents the entry point of the plugin.
 */
public final class BetterPvP extends JavaPlugin {

    // Console command issuer instance
    ConsoleCommandSender mycmd = Bukkit.getConsoleSender();

    // Plugin prefix for chat messages
    public static String prefix;

    // Single instance of the plugin for global access
    public static BetterPvP plugin;

    /**
     * Method called when the plugin is enabled.
     * Here the initial configuration is performed, commands and events are logged, and a startup message is displayed on the console.
     */
    @Override
    public void onEnable() {
        // Setting the plugin prefix from the configuration file
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "&8[&3BetterPvP&8]"));

        // Configure the configuration file and copy default values
        ConfigManager.setupConfig(this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Log commands, events and load configuration
        registerCommands();
        plugin = this;
        registerEvents();
        reloadConfig();

        // Show startup message in the console
        mycmd.sendMessage(ChatUtils.getColoredMessage("     &3_____"));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|   __  \\  &3BetterPvP &7v1.0.0        "));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|  |  | |  &7Running on Bukkit - Paper  "));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|  |___  |    &fPlugin by &3[srstaff_tv, sstudios, 1vcb]"));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|  |__| |  "));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|_____ /   "));
        mycmd.sendMessage(ChatUtils.getColoredMessage(""));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&7Commands successfully loaded"));
    }

    /**
     * Method called when the plugin is disabled.
     * Here the configuration is saved and a deactivation message is displayed on the console.
     */
    @Override
    public void onDisable() {
        // Save configuration when deactivating the plugin
        saveConfig();

        // Display deactivation message in the console
        mycmd.sendMessage(ChatUtils.getColoredMessage("&3BetterPvP is being disabled; this does not affect anything."));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&7Goodbye!"));
    }

    /**
     * Registers custom plugin commands.
     */
    public void registerCommands() {
        this.getCommand("betterpvp").setExecutor(new reload(this));
        this.getCommand("nopvp").setExecutor(new nopvp(this));
    }

    /**
     * Logs custom plugin events.
     */
    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new AntiKillAbuseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    /**
     * Gets the single instance of the plugin.
     * @return BetterPvP instance.
     */
    public static BetterPvP getPlugin() {
        return plugin;
    }
}
