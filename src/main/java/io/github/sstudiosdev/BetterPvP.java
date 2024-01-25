package io.github.sstudiosdev;

import io.github.sstudiosdev.command.BetterPvPReloadCommand;
import io.github.sstudiosdev.listener.AntiKillAbuseListener;
import io.github.sstudiosdev.listener.PlayerDeathListener;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.CommandMapUtil;
import io.github.sstudiosdev.util.constructors.Config;
import io.github.sstudiosdev.vault.VaultHookManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Clase principal del plugin BetterPvP.
 */
public final class BetterPvP extends JavaPlugin {

    public static String prefix;
    @Getter
    private Config mainConfig;

    @Getter
    private VaultHookManager vaultHookManager;

    /**
     * Método llamado cuando se habilita el plugin.
     */
    @Override
    public void onEnable() {
        // Cargar configuración
        loadConfiguration();

        // Registrar eventos
        registerEvents();

        // Registrar managers
        loadManagers();

        // Mostrar información en la consola
        displayConsoleInfo();
    }

    /**
     * Método llamado cuando se deshabilita el plugin.
     */
    @Override
    public void onDisable() {
        sendMessageToConsole("&3BetterPvP is being disabled; this does not affect anything.");
        sendMessageToConsole("&7Goodbye!");
    }

    /**
     * Carga los managers necesarios para el funcionamiento del plugin.
     */
    private void loadManagers() {
        vaultHookManager = new VaultHookManager();
    }

    /**
     * Carga la configuración del plugin.
     */
    private void loadConfiguration() {
        // Inicializar la clase Config
        mainConfig = new Config(this, "config");
        mainConfig.load();

        // Obtener prefijo desde la configuración
        prefix = mainConfig.getString("prefix");
    }

    /**
     * Registra los eventos del plugin.
     */
    private void registerEvents() {
        // Registrar los listeners de eventos
        getServer().getPluginManager().registerEvents(new AntiKillAbuseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);

        CommandMapUtil.registerCommand(this, new BetterPvPReloadCommand(this));
        CommandMapUtil.registerCommand(this, new BetterPvPNoPvPCommand(this));
    }

    /**
     * Muestra información en la consola al habilitar el plugin.
     */
    private void displayConsoleInfo() {
        sendMessageToConsole("     &3_____");
        sendMessageToConsole("   &3|   __  \\  &3BetterPvP &7v1.0.0        ");
        sendMessageToConsole("   &3|  |  | |  &7Running on Bukkit - Paper  ");
        sendMessageToConsole("   &3|  |___  |    &fPlugin by &3[srstaff_tv, sstudios, 1vcb, Updated by pichema]");
        sendMessageToConsole("   &3|  |__| |  ");
        sendMessageToConsole("   &3|_____ /   ");
        sendMessageToConsole("");
        sendMessageToConsole("&7Commands successfully loaded");
        sendMessageToConsole("&7Thank you &c pichema");
    }

    /**
     * Envía un mensaje formateado a la consola.
     *
     * @param message Mensaje a enviar a la consola.
     */
    private void sendMessageToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColorUtil.colorize(message));
    }
}
