package io.github.sstudiosdev.listener;

import io.github.sstudiosdev.BetterPvP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiKillAbuseListener implements Listener {

    private final Map<String, Integer> killCounts = new HashMap<>();
    private final BetterPvP betterPvP;
    private int maxKillsBeforeAutoKill;
    private int resetTimeInSeconds;
    private boolean isEnabled;
    private List<String> abuseCommands;

    public AntiKillAbuseListener(BetterPvP betterPvP) {
        this.betterPvP = betterPvP;
        // Cargar configuración del plugin al inicializar el listener
        loadConfig();

        // Programar reinicio del contador cada cierto tiempo si la funcionalidad está habilitada
        if (isEnabled) {
            Bukkit.getScheduler().runTaskTimer(betterPvP, this::resetKillCounts, 0, resetTimeInSeconds * 20L);
        }
    }

    /**
     * Carga la configuración del plugin desde el archivo de configuración.
     */
    private void loadConfig() {
        maxKillsBeforeAutoKill = betterPvP.getMainConfig().getInt("anti-kill-abuse.max-kill");
        resetTimeInSeconds = betterPvP.getMainConfig().getInt("anti-kill-abuse.time-limit");
        isEnabled = betterPvP.getMainConfig().getBoolean("anti-kill-abuse.enabled");
        abuseCommands = betterPvP.getMainConfig().getStringList("anti-kill-abuse.command-sanction");
    }

    /**
     * Reinicia todos los contadores de asesinatos.
     */
    private void resetKillCounts() {
        killCounts.clear();
    }

    /**
     * Maneja el evento cuando un jugador muere.
     *
     * @param event El evento de muerte del jugador.
     */
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

    /**
     * Ejecuta los comandos de sanción en caso de abuso por parte del jugador.
     *
     * @param player El jugador que ha abusado.
     */
    private void executeAbuseCommands(Player player) {
        for (String command : abuseCommands) {
            command = command.replace("%player_name%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

}
