package io.github.sstudiosdev.command;

import io.github.sstudiosdev.BetterPvP;
import io.github.sstudiosdev.util.ChatColorUtil;
import io.github.sstudiosdev.util.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterPvPNoPvPCommand extends BaseCommand implements Listener {
    private final BetterPvP betterPvP;
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private final List<String> pvpChangeLog = new ArrayList<>();
    private boolean pvpEnabled = true;
    private boolean enablePickupEvent;
    private boolean pvpAutoEnabled = true;
    private BukkitTask pvpAutoEnableTask;
    private final Map<Player, Long> pickupCooldowns = new HashMap<>();
    private final long pickupCooldownTime = 10000;

    public BetterPvPNoPvPCommand(final BetterPvP betterPvP) {
        super("pvp", new ArrayList<>(), "betterpvp.pvp", true);
        this.betterPvP = betterPvP;
        this.enablePickupEvent = betterPvP.getMainConfig().getBoolean("tools.enable-pickup-event");

        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("history")) {
            showChangeLog(sender);
            return;
        }

        if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            if (hasPermission(sender)) {
                if (args[0].equalsIgnoreCase("on") && pvpEnabled) {
                    String pvpreadyEnabled = betterPvP.getMainConfig().getString("pvp-already-enabled");
                    sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpreadyEnabled));
                    return;
                } else if (args[0].equalsIgnoreCase("off") && !pvpEnabled) {
                    String pvpalreadyDisabled = betterPvP.getMainConfig().getString("pvp-already-disabled");
                    sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpalreadyDisabled));
                    return;
                }

                if (args[0].equalsIgnoreCase("off") && sender instanceof Player) {
                    Player player = (Player) sender;
                    long currentTime = System.currentTimeMillis();
                    long defaultCooldown = 60L;
                    long cooldownTime = betterPvP.getMainConfig().getLong("cooldown.pvp-cooldown", defaultCooldown) * 1000;
                    if (cooldowns.containsKey(player) && cooldowns.get(player) + cooldownTime > currentTime) {
                        String CooldownError = betterPvP.getMainConfig().getString("cooldown-error-message");
                        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + CooldownError));
                        return;
                    }
                    cooldowns.put(player, currentTime);
                }

                String pvpToggleMessage = betterPvP.getMainConfig().getString("pvptoggle");
                pvpToggleMessage = pvpToggleMessage.replace("%status%", args[0]);

                pvpEnabled = args[0].equalsIgnoreCase("on");

                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpToggleMessage));

                if (args[0].equalsIgnoreCase("off")) {
                    pvpAutoEnabled = false;
                    if (pvpAutoEnableTask != null) {
                        pvpAutoEnableTask.cancel();
                    }
                    long autoEnableTime = betterPvP.getMainConfig().getLong("cooldown.pvp-auto-enable-time", 300); // 300 seconds by default
                    pvpAutoEnableTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            pvpEnabled = true;
                            pvpAutoEnabled = true;
                            pvpAutoEnableTask = null;
                            String reactivateMessage = betterPvP.getMainConfig().getString("pvp-reactivate-message");
                            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + reactivateMessage));
                        }
                    }.runTaskLater(betterPvP, autoEnableTime * 20); // Convert seconds to ticks
                }

                // Registro de cambios
                String status = args[0].equalsIgnoreCase("on") ? "enabled" : "disabled";
                String message = String.format("PvP %s by %s at %s", status, sender.getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                pvpChangeLog.add(message);
            } else {
                sendNoPermissionMessage(sender);
            }
        } else {
            sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " Usage: /pvp <on/off>"));
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
        if (!enablePickupEvent) {
            return;
        }

        Player player = event.getPlayer();
        if (!pvpEnabled) {
            if (!pickupCooldowns.containsKey(player) || pickupCooldowns.get(player) + pickupCooldownTime < System.currentTimeMillis()) {
                pickupCooldowns.put(player, System.currentTimeMillis());
                String pickupDisabledMessage = betterPvP.getMainConfig().getString("pickup-disabled-message");
                player.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pickupDisabledMessage));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!pvpEnabled && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                    event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) {
                event.setCancelled(true);
            }
        }
    }

    public void showChangeLog(CommandSender sender) {
        String PvPHisory = betterPvP.getMainConfig().getString("pvp-history");
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + PvPHisory));
        for (String entry : pvpChangeLog) {
            sender.sendMessage(ChatColorUtil.colorize(" - " + entry));
        }
    }
}
