package io.github.sstudiosdev.listener;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.constructors.SoundConfig;
import io.github.sstudiosdev.vault.economy.EconomyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final BetterPvP betterPvP;
    private double defaultReward;
    private String killMessage;
    private boolean rewardsEnabled;
    private SoundConfig soundConfig = SoundConfig.getInstance();

    public PlayerDeathListener(BetterPvP betterPvP) {
        this.betterPvP = betterPvP;

        // Cargar configuración al inicializar el listener
        loadConfig();
    }

    /**
     * Cargar la configuración relacionada con las recompensas por muerte de jugador.
     */
    private void loadConfig() {
        rewardsEnabled = betterPvP.getMainConfig().getBoolean("player-kills.enabled");
        defaultReward = betterPvP.getMainConfig().getDouble("player-kills.money-reward");
        killMessage = betterPvP.getMainConfig().getString("kill-reward");
    }

    /**
     * Maneja el evento de muerte de jugador.
     *
     * @param event El evento de muerte del jugador.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Verificar si las recompensas están habilitadas
        if (!rewardsEnabled) {
            return;
        }

        // Verificar si el causante de la muerte fue otro jugador
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();

            // Verificar si la integración con Vault se ha configurado correctamente
            if (!betterPvP.getVaultHookManager().setupVaultEconomy()) {
                return;
            }

            // Obtener el gestor de economía de Vault
            final EconomyManager economyManager = betterPvP.getVaultHookManager().getEconomyManager();

            // Depositar la recompensa en la cuenta del jugador asesino
            economyManager.depositMoney(killer, defaultReward);

            // Enviar un mensaje al jugador asesino sobre la recompensa
            String formattedMessage = killMessage.replace("%bt-give-Money%", String.valueOf(defaultReward));
            killer.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + formattedMessage));

            killer.playSound(killer.getLocation(), soundConfig.getSound("money-reward"), 1.0f, 1.0f);
        }
    }
}
