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
        loadConfig();
    }

    /**
     * Load the configuration related to player death rewards.
     */
    private void loadConfig() {
        rewardsEnabled = betterPvP.getMainConfig().getBoolean("player-kills.enabled");
        defaultReward = betterPvP.getMainConfig().getDouble("player-kills.money-reward");
        killMessage = betterPvP.getMainConfig().getString("kill-reward");
    }

    /**
     * Handle the player death event.
     *
     * @param event The player death event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Verify if rewards are enabled
        if (!rewardsEnabled) {
            return;
        }

        // Verify if the cause of death was another player
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            if (killer != event.getEntity()) {
                // Verify if Vault integration is set up correctly
                if (!betterPvP.getVaultHookManager().setupVaultEconomy()) {
                    return;
                }

                // Get the Vault economy manager
                final EconomyManager economyManager = betterPvP.getVaultHookManager().getEconomyManager();

                // Deposit the reward into the killer's account
                economyManager.depositMoney(killer, defaultReward);

                // Send a message to the killer about the reward
                if (killMessage != null) {
                    String formattedMessage = killMessage.replace("%bt-give-Money%", String.valueOf(defaultReward));
                    killer.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + formattedMessage));
                }

                // Play the reward sound for the killer
                killer.playSound(killer.getLocation(), soundConfig.getSound("money-reward"), 1.0f, 1.0f);
            } else {
                // The player killed themselves
                String killError = betterPvP.getMainConfig().getString("self-kill-message");
                killer.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + killError));

                killer.playSound(killer.getLocation(), soundConfig.getSound("self-kill"), 1.0f, 1.0f);
            }
        }
    }
}
