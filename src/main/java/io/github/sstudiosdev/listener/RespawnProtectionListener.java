package io.github.sstudiosdev.listener;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.constructors.SoundConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RespawnProtectionListener implements Listener {
    private final Set<Player> playersWithRespawnProtection = new HashSet<>();
    private final Map<Player, ArmorStand> protectionArmorStands = new HashMap<>();
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
            ArmorStand stand = protectionArmorStands.remove(player);
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
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

        // Create Armor Stand if animation or shield is enabled
        boolean isAnimationEnabled = config.getBoolean("respawn-protection.animation.enabled", true);
        boolean isShieldEnabled = config.getBoolean("respawn-protection.shield.enabled", true);

        ArmorStand armorStand = null;
        if (isAnimationEnabled || isShieldEnabled) {
            armorStand = player.getWorld().spawn(player.getLocation().add(0, 0.75, 0), ArmorStand.class);
            armorStand.setBasePlate(false);
            armorStand.setMarker(true);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            if (isShieldEnabled) {
                armorStand.setHelmet(new ItemStack(Material.SHIELD));
            }
            protectionArmorStands.put(player, armorStand);
        }

        // Run animation task if enabled
        if (isAnimationEnabled && armorStand != null) {
            ArmorStand finalArmorStand = armorStand;
            Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                int tick = 0;
                @Override
                public void run() {
                    if (!playersWithRespawnProtection.contains(player)) {
                        this.cancel();
                        return;
                    }
                    tick++;
                    double angle = Math.toRadians((tick * 10) % 360);
                    finalArmorStand.setHeadPose(new EulerAngle(angle, angle, angle));
                }

                private void cancel() {
                    Bukkit.getScheduler().cancelTask(this.hashCode());
                }
            }, 0L, 1L);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playersWithRespawnProtection.remove(player);
            String respawnMessage = ChatColorUtil.colorize(BetterPvP.prefix + " " + config.getString("respawn_message"));
            player.sendMessage(respawnMessage);
            player.playSound(player.getLocation(), soundConfig.getSound("respawn-player"), 1.0f, 1.0f);

            // Remove Armor Stand
            ArmorStand stand = protectionArmorStands.remove(player);
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ArmorStand armorStand = protectionArmorStands.get(player);
        if (armorStand != null) {
            // Set the position of the Armor Stand to be above the player's head
            armorStand.teleport(player.getLocation().add(0, 0.75, 0));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (playersWithRespawnProtection.contains(player)) {
            playersWithRespawnProtection.remove(player);
            ArmorStand stand = protectionArmorStands.remove(player);
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
    }
}
