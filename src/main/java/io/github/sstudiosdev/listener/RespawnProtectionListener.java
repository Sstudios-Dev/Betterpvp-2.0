package io.github.sstudiosdev.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private Set<Player> playersWithRespawnProtection = new HashSet<>();

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
        playersWithRespawnProtection.add(player);
        Bukkit.getScheduler().runTaskLater((JavaPlugin) Bukkit.getPluginManager().getPlugin("BetterPvP"), () -> {
            playersWithRespawnProtection.remove(player);
            player.sendMessage(ChatColor.GREEN + "¡Has terminado la protección de respawn!");
        }, 100); // 5 segundos (20 ticks por segundo, 100 ticks = 5 segundos)
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (playersWithRespawnProtection.contains(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "¡No puedes recibir daño mientras tienes protección de respawn!");
            }
        }
    }
}
