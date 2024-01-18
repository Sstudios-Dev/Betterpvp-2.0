package Listener;

import github.betterpvp.BetterPvP;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import utils.ChatUtils;

public class PlayerDeathListener implements Listener {

    private final JavaPlugin plugin;
    private final double defaultReward;
    private final String killMessage;

    public PlayerDeathListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaultReward = plugin.getConfig().getDouble("money-reward", 10.0);
        this.killMessage = plugin.getConfig().getString("kill-reward", "&aYou have received %amount% for killing a player!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Verificar si el causante de la muerte fue otro jugador
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();

            // Recargar la configuración antes de obtener los valores
            plugin.reloadConfig();

            // Lógica para otorgar dinero usando Vault
            if (setupEconomy()) {
                Economy economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
                double amount = plugin.getConfig().getDouble("reward-per-kill", defaultReward);

                economy.depositPlayer(killer, amount);

                // Enviar el mensaje al jugador asesino
                String formattedMessage = killMessage.replace("%amount%", String.valueOf(amount));
                killer.sendMessage(ChatUtils.getColoredMessage(BetterPvP.prefix + " " + killMessage));
            }
        }
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        return plugin.getServer().getServicesManager().getRegistration(Economy.class) != null;
    }
}