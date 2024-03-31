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
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetterPvPNoPvPCommand extends BaseCommand implements Listener {
    private final BetterPvP betterPvP;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    private boolean pvpEnabled = true;

    public BetterPvPNoPvPCommand(final BetterPvP betterPvP) {
        super("pvp", new ArrayList<>(), "betterpvp.pvp", true);
        this.betterPvP = betterPvP;

        PluginManager pluginManager = betterPvP.getServer().getPluginManager();
        pluginManager.registerEvents(this, betterPvP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Verify whether the "on" or "off" argument was provided.
        if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            if (hasPermission(sender)) {
                if (args[0].equalsIgnoreCase("off") && sender instanceof Player) {
                    Player player = (Player) sender;
                    long currentTime = System.currentTimeMillis();
                    long defaultCooldown = 60L; // Default value in seconds
                    long cooldownTime = betterPvP.getMainConfig().getLong("cooldown.pvp-cooldown", defaultCooldown) * 1000;
                    if (cooldowns.containsKey(player) && cooldowns.get(player) + cooldownTime > currentTime) {
                        String CooldownError = betterPvP.getMainConfig().getString("cooldown-error-message");
                        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + CooldownError));
                        return;
                    }
                    cooldowns.put(player, currentTime);
                }

                // Get the message from the configuration
                String pvpToggleMessage = betterPvP.getMainConfig().getString("pvptoggle");

                // Replace "%status%" with the status provided in the command
                pvpToggleMessage = pvpToggleMessage.replace("%status%", args[0]);

                pvpEnabled = args[0].equalsIgnoreCase("on");

                // Send success message
                sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + pvpToggleMessage));
            } else {
                sendNoPermissionMessage(sender);
            }
        } else {
            // Incorrect use message
            sender.sendMessage(ChatColor.RED + "Usage: /pvp <on/off>");
        }
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("on");
            completions.add("off");
        }
        return completions;
    }

    /**
     * Verify if the player has the necessary permissions.
     *
     * @param sender The sender of the command.
     * @return True if the player has permissions, false otherwise.
     */
    private boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("betterpvp.pvp") || sender instanceof ConsoleCommandSender || sender.isOp();
    }

    /**
     * Sends the lack of permissions message to the player.
     *
     * @param sender The sender of the command.
     */
    private void sendNoPermissionMessage(CommandSender sender) {
        String noPermissionMessage = betterPvP.getMainConfig().getString("no-permission");
        noPermissionMessage = noPermissionMessage.replace("%player_name%", sender.getName());
        sender.sendMessage(ChatColorUtil.colorize(BetterPvP.prefix + " " + noPermissionMessage));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!pvpEnabled && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                event.setCancelled(true);
            }
        }
    }
}
