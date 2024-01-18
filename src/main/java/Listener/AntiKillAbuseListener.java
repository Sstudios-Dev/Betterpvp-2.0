package Listener;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiKillAbuseListener implements Listener {

    private Map<String, Integer> killCounts = new HashMap<>();
    private int maxKillsBeforeAutoKill;
    private int resetTimeInSeconds;
    private boolean isEnabled;
    private List<String> abuseCommands;
    private JavaPlugin plugin;

    public AntiKillAbuseListener(JavaPlugin plugin) {
        this.plugin = plugin;
        // Cargar configuración del plugin
        loadConfig();

        // Programar reinicio del contador cada cierto tiempo si la funcionalidad está habilitada
        if (isEnabled) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::resetKillCounts, 0, resetTimeInSeconds * 20L);
        }
    }

    private void loadConfig() {
        // Recargar configuración del plugin
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Cargar límite de asesinatos, tiempo de reinicio, estado de la funcionalidad y comandos en caso de abuso desde la configuración
        maxKillsBeforeAutoKill = config.getInt("max-kill", 5);
        resetTimeInSeconds = config.getInt("time-limit", 300);
        isEnabled = config.getBoolean("enabled", true);
        abuseCommands = config.getStringList("command-sanction");
    }

    private void resetKillCounts() {
        // Reiniciar todos los contadores de asesinatos
        killCounts.clear();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isEnabled) {
            return; // No realizar ninguna acción si la funcionalidad está desactivada
        }

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            String killerName = killer.getName();

            // Incrementar el contador de asesinatos del jugador
            int kills = killCounts.getOrDefault(killerName, 0) + 1;
            killCounts.put(killerName, kills);

            if (kills >= maxKillsBeforeAutoKill) {
                // Ejecutar comandos en caso de abuso
                executeAbuseCommands(killer);

                // Reiniciar el contador de asesinatos.
                killCounts.put(killerName, 0);
            }
        }
    }

    private void executeAbuseCommands(Player player) {
        for (String command : abuseCommands) {
            command = command.replace("%player_name%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
