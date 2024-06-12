package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import io.github.sstudiosdev.util.constructors.SoundConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Sound;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterPvPNoPvPCommand extends BaseCommand implements Listener {
    private final BetterPvP betterPvP;
    private final Map<Player, Boolean> pvpStatus = new HashMap<>();
    private Map<Player, Long> cooldowns = new HashMap<>();
    private final List<String> pvpChangeLog = new ArrayList<>();
    private final Map<Player, Boolean> pvpAutoEnabled = new HashMap<>();
    private final Map<Player, BukkitTask> pvpAutoEnableTask = new HashMap<>();
    private final Map<Player, Long> pickupCooldowns = new HashMap<>();
    private final long pickupCooldownTime = 10000;
    private final Map<Player, Long> lastPickupMessageTime = new HashMap<>();
    private final Map<Player, BossBar> autoEnableBossBars = new HashMap<>();

    public BetterPvPNoPvPCommand(final BetterPvP betterPvP) {
        super("pvp", new ArrayList<>(), "betterpvp.pvp", true);
        this.betterPvP = betterPvP;

        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase("history")) {
            showChangeLog(sender, player);
            return;
        }

        if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            if (hasPermission(sender)) {
                boolean pvpEnabledForPlayer = pvpStatus.getOrDefault(player, true);

                if (args[0].equalsIgnoreCase("on") && pvpEnabledForPlayer) {
                    String pvpReadyEnabled = betterPvP.getMainConfig().getString("pvp-already-enabled");
                    sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpReadyEnabled));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    return;
                } else if (args[0].equalsIgnoreCase("off") && !pvpEnabledForPlayer) {
                    String pvpAlreadyDisabled = betterPvP.getMainConfig().getString("pvp-already-disabled");
                    sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpAlreadyDisabled));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    return;
                }

                if (args[0].equalsIgnoreCase("off")) {
                    // Check cooldown
                    long currentTime = System.currentTimeMillis();
                    long defaultCooldown = 60L;
                    long cooldownTime = betterPvP.getMainConfig().getLong("cooldown.pvp-cooldown", defaultCooldown) * 1000;
                    if (cooldowns.containsKey(player) && cooldowns.get(player) + cooldownTime > currentTime) {
                        String cooldownError = betterPvP.getMainConfig().getString("cooldown-error-message");
                        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + cooldownError));

                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
                        return;
                    }

                    // Update BossBar and PvP status
                    BossBar bossBar = autoEnableBossBars.get(player);
                    if (bossBar == null) {
                        List<String> defaultBossBarColors = betterPvP.getMainConfig().getStringList("bossbar.default-colors");
                        if (defaultBossBarColors.isEmpty()) {
                            defaultBossBarColors.add("RED");
                        }
                        BarColor bossBarColor = BarColor.valueOf(defaultBossBarColors.get(0));
                        bossBar = Bukkit.createBossBar("PvP will activate in 5 minutes", bossBarColor, BarStyle.SOLID);
                        autoEnableBossBars.put(player, bossBar);
                    }
                    bossBar.addPlayer(player);
                    bossBar.setVisible(true);
                    pvpStatus.put(player, false);

                    // Cancel automatic PvP enable task
                    pvpAutoEnabled.put(player, false);
                    BukkitTask autoEnableTask = pvpAutoEnableTask.get(player);
                    if (autoEnableTask != null) {
                        autoEnableTask.cancel();
                    }
                    startAutoEnableBossBar(player);
                    // Set cooldown for the player
                    cooldowns.put(player, currentTime);
                }

                if (args[0].equalsIgnoreCase("on")) {
                    BossBar bossBar = autoEnableBossBars.get(player);
                    if (bossBar != null) {
                        bossBar.removePlayer(player);
                        bossBar.setVisible(false);
                    }
                    pvpStatus.put(player, true);
                }

                String pvpToggleMessage = betterPvP.getMainConfig().getString("pvptoggle");
                pvpToggleMessage = pvpToggleMessage.replace("%bt-status%", args[0]);

                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpToggleMessage));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                String status = args[0].equalsIgnoreCase("on") ? "enabled" : "disabled";
                String message = String.format("PvP %s by %s at %s", status, sender.getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                pvpChangeLog.add(message);
            } else {
                sendNoPermissionMessage(sender);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        } else {
            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " Usage: /pvp <on/off>"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
        }
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("on");
            completions.add("off");
            completions.add("history");
        }
        return completions;
    }

    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.pvp") || sender instanceof ConsoleCommandSender || sender.isOp();
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        String noPermissionMessage = betterPvP.getMainConfig().getString("no-permission");
        noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + noPermissionMessage));
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!pvpStatus.getOrDefault(player, true)) {
            if (!pvpAutoEnabled.getOrDefault(player, true)) {
                event.setCancelled(true);
                String pickupDisabledMessage = betterPvP.getMainConfig().getString("pickup-disabled-message");
                long currentTime = System.currentTimeMillis();

                // Send the message immediately if it's the first time or if 5 seconds have passed since the last message
                if (!lastPickupMessageTime.containsKey(player) || currentTime - lastPickupMessageTime.get(player) >= 5000) {
                    lastPickupMessageTime.put(player, currentTime);
                    player.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pickupDisabledMessage));
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!pvpStatus.getOrDefault(player, true) && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                    event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player target = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();
            if (!pvpStatus.getOrDefault(target, true) || !pvpStatus.getOrDefault(attacker, true)) {
                event.setCancelled(true);
            }
        }
    }

    public void showChangeLog(CommandSender sender, Player player) {
        String pvpHistory = betterPvP.getMainConfig().getString("pvp-history");
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpHistory));
        for (String entry : pvpChangeLog) {
            if (entry.contains(player.getName())) {
                sender.sendMessage(ChatColorUtil.colorize(" - " + entry));
            }
        }
    }

    private void startAutoEnableBossBar(Player player) {
        BukkitTask autoEnableTask = pvpAutoEnableTask.get(player);
        if (autoEnableTask != null) {
            autoEnableTask.cancel();
        }
        long autoEnableTime = betterPvP.getMainConfig().getLong("cooldown.pvp-auto-enable-time", 300);
        autoEnableTask = new BukkitRunnable() {
            int timeLeft = (int) autoEnableTime;

            @Override
            public void run() {
                if (timeLeft > 0) {
                    String autoEnableTitle = betterPvP.getMainConfig().getString("bossbar.title");
                    BossBar bossBar = autoEnableBossBars.get(player);

                    bossBar.setTitle(ChatColorUtil.colorize(autoEnableTitle.replace("%time%", String.valueOf(timeLeft))));
                    bossBar.setProgress((double) timeLeft / autoEnableTime);
                    timeLeft--;
                } else {
                    pvpStatus.put(player, true);
                    pvpAutoEnabled.put(player, true);
                    pvpAutoEnableTask.put(player, null);
                    BossBar bossBar = autoEnableBossBars.get(player);
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    String reactivateMessage = betterPvP.getMainConfig().getString("pvp-reactivate");

                    player.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + reactivateMessage));

                    cancel();
                }
            }
        }.runTaskTimer(betterPvP, 0, 20);
        pvpAutoEnableTask.put(player, autoEnableTask);
    }
}
