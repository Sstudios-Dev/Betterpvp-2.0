package io.github.sstudiosdev.listener;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
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

        playersWithRespawnProtection.add(player);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playersWithRespawnProtection.remove(player);
            String respawnMessage = ChatColorUtil.colorize(BetterPvP.prefix + " " + config.getString("respawn_message"));
            player.sendMessage(respawnMessage);
        }, 100); // 5 seconds (20 ticks per second, 100 ticks = 5 seconds)
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (playersWithRespawnProtection.contains(player)) {
                event.setCancelled(true);
                String protectionMessage = ChatColorUtil.colorize(BetterPvP.prefix + " " + config.getString("protection_message"));
                player.sendMessage(protectionMessage);
            }
        }
    }
}
