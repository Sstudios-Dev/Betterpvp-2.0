package github.betterpvp;

import Listener.AntiKillAbuseListener;
import Listener.PlayerDeathListener;
import commands.reload;
import manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import utils.ChatUtils;


public final class BetterPvP extends JavaPlugin {

    ConsoleCommandSender mycmd = Bukkit.getConsoleSender();
    public static String prefix;

    public static BetterPvP plugin;

    @Override
    public void onEnable() {

        //prefix
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "&8[&3BetterPvP&8]"));

        //config
        ConfigManager.setupConfig(this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        //Register
        registerCommands();
        plugin = this;
        registerEvents();
        reloadConfig();

        mycmd.sendMessage(ChatUtils.getColoredMessage("     &3_____"));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|   __  \\  &3BetterPvP &7v1.0.0        "));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|  |  | |  &7Running on Bukkit - Paper  "));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|  |___  |    &fPlugin by &3[srstaff_tv, sstudios, 1vcb]"));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|  |__| |  "));
        mycmd.sendMessage(ChatUtils.getColoredMessage("   &3|_____ /   "));
        mycmd.sendMessage(ChatUtils.getColoredMessage(""));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&7Commands successfully loaded"));

    }

    @Override
    public void onDisable() {

        // Guardar configuración al desactivar el plugin
        saveConfig();

        mycmd.sendMessage(ChatUtils.getColoredMessage("&3BetterPvP is being disabled this does not affect anything."));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&7Commands Saved Successfully"));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&7Modules saved Successfully"));
        mycmd.sendMessage(ChatUtils.getColoredMessage("&7Goodbye!"));

    }

    public void registerCommands() {
        this.getCommand("betterpvp").setExecutor(new reload(this));
        this.getCommand("nopvp").setExecutor(new nopvp(this));
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new AntiKillAbuseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }

    public static BetterPvP getPlugin() {
        return plugin;
    }

}
