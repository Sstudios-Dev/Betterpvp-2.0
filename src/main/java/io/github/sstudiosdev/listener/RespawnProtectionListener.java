package io.github.sstudiosdev.listener;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.constructors.SoundConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class RespawnProtectionListener implements Listener {
    private final Set<Player> playersWithRespawnProtection = new HashSet<>();
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private SoundConfig soundConfig = SoundConfig.getInstance();

    public RespawnProtectionListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (playersWithRespawnProtection.contains(player)) {
            playersWithRespawnProtection.remove(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (!config.getBoolean("respawn-protection.enabled", true)) {
            return;
        }

        playersWithRespawnProtection.add(player); // Add player to set
        int duration = config.getInt("respawn-protection.duration", 5); // Get duration from config, default to 5 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playersWithRespawnProtection.remove(player);
            String respawnMessage = ChatColorUtil.colorize(BetterPvP.prefix + " " + config.getString("respawn_message"));
            player.sendMessage(respawnMessage);
            player.playSound(player.getLocation(), soundConfig.getSound("respawn-player"), 1.0f, 1.0f);
        }, duration * 20L); // Convert seconds to ticks
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (playersWithRespawnProtection.contains(player)) {
                event.setCancelled(true);
                String protectionMessage = ChatColorUtil.colorize(BetterPvP.prefix + " " + config.getString("protection_message"));
                player.sendMessage(protectionMessage);
                player.playSound(player.getLocation(), soundConfig.getSound("protection-attack"), 1.0f, 1.0f);
            }
        }
    }
}